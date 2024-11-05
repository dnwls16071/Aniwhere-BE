package com.example.aniwhere.infrastructure.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("jwt")
public class JwtProperties {
	private String issuer;
	private String secretKey;
	private Long access_token_expiration_time;
	private Long refresh_token_expiration_time;
}