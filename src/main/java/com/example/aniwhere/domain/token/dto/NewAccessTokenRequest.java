package com.example.aniwhere.domain.token.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewAccessTokenRequest {
	private String refreshToken;
}
