package com.example.aniwhere.infrastructure.oauth2;

import com.example.aniwhere.domain.user.Sex;

import java.util.Map;

public class KakaoUserInfo implements ExtendedKakaoOAuth2UserInfo {

	private Map<String, Object> attributes;
	private Map<String, Object> attributesAccount;
	private Map<String, Object> attributesProfile;

	public KakaoUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
		this.attributesAccount = (Map<String, Object>) attributes.get("kakao_account");
		this.attributesProfile = (Map<String, Object>) attributesAccount.get("profile");
	}

	@Override
	public String getBirthYear() {
		return attributesAccount.get("birthyear").toString();
	}

	@Override
	public String getBirthDay() {
		return attributesAccount.get("birthday").toString();
	}

	@Override
	public Sex getGender() {
		String gender = attributesAccount.get("gender").toString();
		return Sex.valueOf(gender);
	}

	@Override
	public String getEmail() {
		return (String) attributesAccount.get("email");
	}

	@Override
	public String getName() {
		return (String) attributesProfile.get("nickname");
	}
}
