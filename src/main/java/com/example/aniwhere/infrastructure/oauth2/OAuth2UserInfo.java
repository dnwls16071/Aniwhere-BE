package com.example.aniwhere.infrastructure.oauth2;

public interface OAuth2UserInfo {

	String getNickname();	// 닉네임
	String getProviderId();	// 소셜 로그인 식별 값
	String getProvider();	// 제공자
	String getEmail();		// 이메일
	String getBirthyear();  // 출생년도
	String getBirthday();   // 출생월일
	String getSex();        // 성별
}