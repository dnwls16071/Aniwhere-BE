package com.example.aniwhere.application.user;

import com.example.aniwhere.application.cache.RedisService;
import com.example.aniwhere.domain.token.dto.JwtToken;
import com.example.aniwhere.infrastructure.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoService {

	private final RedisService redisService;
	private final TokenProvider tokenProvider;

	public void kakaoLogout(String accessToken, String refreshToken) {

		String email = tokenProvider.getEmail(accessToken);
		String oAuthToken = redisService.getOAuthToken(email);

		if (oAuthToken == null) {
			redisService.saveBlackListJwtToken(email, new JwtToken(accessToken, refreshToken));
			return;
		}

		try {
			RestTemplate restTemplate = new RestTemplate();
			String kakaoLogoutUrl = "https://kapi.kakao.com/v1/user/logout";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setBearerAuth(oAuthToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);

			ResponseEntity<String> response = restTemplate.exchange(
					kakaoLogoutUrl,
					HttpMethod.POST,
					entity,
					String.class
			);

			if (response.getStatusCode() == HttpStatus.OK) {
				redisService.deleteOAuthToken(email);
				redisService.saveBlackListJwtToken(email, new JwtToken(accessToken, refreshToken));
			} else {
				throw new RuntimeException("소셜 로그인 계정에 대한 로그아웃에 실패했습니다!");
			}

		} catch (Exception e) {
			// 카카오 로그아웃에 실패하더라도 JWT 토큰은 블랙리스트에 추가
			redisService.saveBlackListJwtToken(email, new JwtToken(accessToken, refreshToken));
			throw new RuntimeException("Failed to logout from Kakao", e);
		}
	}
}
