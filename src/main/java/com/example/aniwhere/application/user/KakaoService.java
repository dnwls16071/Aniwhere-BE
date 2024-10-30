package com.example.aniwhere.application.user;

import com.example.aniwhere.domain.user.dto.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoService {

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String client_id;

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String redirect_uri;

	public UserDTO.KakaoUserInfo kakaoLogin(String authorizationCode, HttpServletResponse response) throws JsonProcessingException {

		// 프론트가 받은 인가 코드를 백엔드에 전달
		// 백엔드에서 OAuth 서버로 인가 코드를 전달하여 OAuth 서버로부터 액세스 토큰을 발급
		String accessToken = getAccessToken(authorizationCode);

		// 발급받은 액세스 토큰을 사용하여 카카오 사용자 정보를 조회하기
		return getKakaoUserInfo(accessToken);
	}

	// Step 1 : 인가 코드 받기
	private String getAccessToken(String authorizationCode) throws JsonProcessingException {

		// HTTP Header
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

		// HTTP Body
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", client_id);
		body.add("redirect_uri", redirect_uri);
		body.add("code", authorizationCode);

		// OAuth 서버로 인가 코드를 포함한 요청 보내기
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
		RestTemplate rt = new RestTemplate();
		ResponseEntity<String> response = rt.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST, kakaoTokenRequest, String.class);

		// HTTP 응답(여러 값들을 응답으로 받지만 그 중에서도 필요한 것만 파싱한다.)
		// accessToken을 파싱
		String responseBody = response.getBody();

		// 객체 직렬화
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(responseBody);

		String accessToken = jsonNode.get("access_token").asText();
		return accessToken;
	}

	// Step 2 : OAuth 서버로부터 액세스 토큰을 발급받고 이 액세스 토큰을 사용해 카카오 사용자 정보를 조회하기
	private UserDTO.KakaoUserInfo getKakaoUserInfo(String accessToken) throws JsonProcessingException {

		// HTTP Header
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

		// HTTP Request
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		RestTemplate rt = new RestTemplate();
		ResponseEntity<String> response = rt.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, request, String.class);

		// HTTP Response
		String responseBody = response.getBody();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(responseBody);

		// 필요한 필드 파싱
		// ID, 프로필 이미지 URL, 이름,
		Long id = jsonNode.get("id").asLong();
		String profile_image_url = jsonNode.get("kakao_account").get("profile").get("profile_image_url").asText();
		String nickname = jsonNode.get("kakao_account").get("profile").get("nickname").asText();
		String email = jsonNode.get("kakao_account").get("email").asText();
		String birthday = jsonNode.get("kakao_account").get("birthday").asText();
		String birthyear = jsonNode.get("kakao_account").get("birthyear").asText();
		String gender = jsonNode.get("kakao_account").get("gender").asText();

		return UserDTO.KakaoUserInfo.builder()
				.id(id)
				.profile_image_url(profile_image_url)
				.nickname(nickname)
				.birthday(birthday)
				.birthyear(birthyear)
				.email(email)
				.gender(gender)
				.build();
	}
}
