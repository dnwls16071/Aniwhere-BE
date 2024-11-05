package com.example.aniwhere.infrastructure.oauth2;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {

	private Map<String, Object> attributes;
	private Map<String, Object> attributesAccount;
	private Map<String, Object> attributesProfile;

	public KakaoUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
		this.attributesAccount = (Map<String, Object>) attributes.get("kakao_account");
		this.attributesProfile = (Map<String, Object>) attributesAccount.get("profile");
	}

	@Override
	public String getNickname() {
		return attributesProfile.get("nickname").toString();
	}

	@Override
	public String getProviderId() {
		return attributes.get("id").toString();
	}

	@Override
	public String getProvider() {
		return "kakao";
	}

	@Override
	public String getEmail() {
		return attributesAccount.get("email").toString();
	}

	@Override
	public String getBirthyear() {
		return attributesAccount.get("birthyear").toString();
	}

	@Override
	public String getBirthday() {
		return attributesAccount.get("birthday").toString();
	}

	@Override
	public String getSex() {
		return attributesAccount.get("gender").toString();
	}
}
