package com.example.aniwhere.application.cache;

import com.example.aniwhere.domain.token.dto.JwtToken;
import com.example.aniwhere.infrastructure.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;
	private final JwtProperties jwtProperties;

	public void saveBlackListJwtToken(String email, JwtToken jwtToken) {
		String accessToken = jwtToken.getAccessToken();
		String refreshToken = jwtToken.getRefreshToken();

		redisTemplate.opsForValue().set(
				"BAT: " + email,
				accessToken,
				jwtProperties.getAccess_token_expiration_time(),
				TimeUnit.MILLISECONDS
		);

		redisTemplate.opsForValue().set(
				"BRT: " + email,
				refreshToken,
				jwtProperties.getRefresh_token_expiration_time(),
				TimeUnit.MILLISECONDS
		);

		redisTemplate.delete("RT: " + email);
	}

	public void saveRefreshToken(String email, String refreshToken) {
		redisTemplate.opsForValue().set(
				"RT: " + email,
				refreshToken,
				jwtProperties.getRefresh_token_expiration_time(),
				TimeUnit.MILLISECONDS
		);
	}

	public String getRefreshToken(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public String getOAuthToken(String email) {
		return redisTemplate.opsForValue().get("OAT: " + email);
	}

	public void saveOAuthToken(String email, String oauthAccessToken) {
		redisTemplate.opsForValue().set(
				"OAT: " + email,
				oauthAccessToken,
				6,
				TimeUnit.HOURS
		);
	}

	public void deleteOAuthToken(String email) {
		redisTemplate.delete("OAT: " + email);
	}

	public void saveAuthCode(String toEmail, String authCode, Duration duration) {
		redisTemplate.opsForValue().set(
				toEmail,
				authCode,
				duration
		);
	}

	public boolean hasAuthCode(String authCode) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(authCode));
	}

	public String getAuthCode(String authCode) {
		return redisTemplate.opsForValue().get(authCode);
	}

	public void deleteAuthCode(String authCode) {
		redisTemplate.delete(authCode);
	}
}
