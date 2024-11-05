package com.example.aniwhere.domain.token.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewAccessTokenResponse {
	private String accessToken;
	private String message;
}
