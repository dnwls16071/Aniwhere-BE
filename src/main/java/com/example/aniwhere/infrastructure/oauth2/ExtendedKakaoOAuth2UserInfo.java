package com.example.aniwhere.infrastructure.oauth2;

import com.example.aniwhere.domain.user.Sex;

public interface ExtendedKakaoOAuth2UserInfo extends OAuth2UserInfo {

	String getBirthYear();
	String getBirthDay();
	Sex getGender();
}
