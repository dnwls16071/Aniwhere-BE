package com.example.aniwhere.application.user;

import com.example.aniwhere.domain.user.MyUserDetails;
import com.example.aniwhere.domain.user.Role;
import com.example.aniwhere.domain.user.Sex;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.infrastructure.oauth2.KakaoUserInfo;
import com.example.aniwhere.infrastructure.oauth2.OAuth2UserInfo;
import com.example.aniwhere.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrincipalOAuthDetailsService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		return processOAuth2User(userRequest, oAuth2User);
	}

	private OAuth2User processOAuth2User(OAuth2UserRequest request, OAuth2User oAuth2User) {
		String oAuthToken = request.getAccessToken().getTokenValue();
		KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

		Optional<User> findUser = userRepository.findByEmail(kakaoUserInfo.getEmail());
		User user;

		if (findUser.isPresent()) {
			user = findUser.get();
			updateUserInfo(user, kakaoUserInfo);
		} else {
			user = User.builder()
					.nickname(kakaoUserInfo.getProvider() + "_" + kakaoUserInfo.getProviderId())
					.email(kakaoUserInfo.getEmail())
					.provider(kakaoUserInfo.getProvider())
					.providerId(kakaoUserInfo.getProviderId())
					.role(Role.ROLE_USER)
					.sex(Sex.valueOf(kakaoUserInfo.getSex()))
					.birthyear(kakaoUserInfo.getBirthyear())
					.birthday(kakaoUserInfo.getBirthday())
					.build();
			userRepository.save(user);
		}

		return new MyUserDetails(user, oAuth2User.getAttributes(), oAuthToken);
	}

	// 스프링 시큐리티 회원가입과 OAuth2 회원가입 시 메일이 중복되는 경우 개인 정보를 업데이트한다.
	private User updateUserInfo(User user, OAuth2UserInfo oAuth2UserInfo) {
		return user.updateUser(
				User.builder()
						.nickname(oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId())
						.email(oAuth2UserInfo.getEmail())
						.provider(oAuth2UserInfo.getProvider())
						.providerId(oAuth2UserInfo.getProviderId())
						.build()
		);
	}
}
