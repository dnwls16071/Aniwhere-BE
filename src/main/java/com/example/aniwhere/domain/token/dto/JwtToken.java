package com.example.aniwhere.domain.token.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class JwtToken {
	private String accessToken;
	private String refreshToken;
}
