package com.example.aniwhere.domain.token.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class JwtToken {
	private String accessToken;
	private String refreshToken;
}
