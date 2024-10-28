package com.example.aniwhere.application.user;

import com.example.aniwhere.domain.user.MyUserDetails;
import com.example.aniwhere.domain.user.Role;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.infrastructure.oauth2.ExtendedKakaoOAuth2UserInfo;
import com.example.aniwhere.infrastructure.oauth2.KakaoUserInfo;
import com.example.aniwhere.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
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
		ExtendedKakaoOAuth2UserInfo oAuth2UserInfo = null;

		oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

		Optional<User> getUser = userRepository.findByEmail(oAuth2UserInfo.getEmail());
		User user;

		if (getUser.isPresent()) {
			user = getUser.get();
		}

		user = User.builder()
				.email(oAuth2UserInfo.getEmail())
				.role(Role.ROLE_USER)
				.birthyear(oAuth2UserInfo.getBirthYear())
				.birthday(oAuth2UserInfo.getBirthDay())
				.sex(oAuth2UserInfo.getGender())
				.build();

		userRepository.save(user);
		return new MyUserDetails(user, oAuth2User.getAttributes());
	}
}
