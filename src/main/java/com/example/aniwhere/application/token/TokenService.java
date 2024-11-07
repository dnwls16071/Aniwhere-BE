package com.example.aniwhere.application.token;

import com.example.aniwhere.application.cache.RedisService;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.ErrorCode;
import com.example.aniwhere.global.error.exception.NotFoundRefreshTokenException;
import com.example.aniwhere.infrastructure.jwt.RefreshTokenService;
import com.example.aniwhere.infrastructure.jwt.TokenProvider;
import com.example.aniwhere.application.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

	private final TokenProvider tokenProvider;
	private final RefreshTokenService refreshTokenService;
	private final RedisService redisService;
	private final UserService userService;

	public String createNewAccessToken(String refreshToken) {
		if (!tokenProvider.validateToken(refreshToken)) {
			throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
		}

		String email = tokenProvider.getEmail(refreshToken);
		String redisKey = "RT: " + email;
		String redisRefreshToken = redisService.getRefreshToken(redisKey);

		if (redisRefreshToken == null || !redisRefreshToken.equals(refreshToken)) {
			throw new NotFoundRefreshTokenException("해당 리프레시 토큰을 찾을 수 없습니다.", ErrorCode.NOT_FOUND_REFRESH_TOKEN);
		}

		Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
		User user = userService.findById(userId);

		return tokenProvider.generateAccessToken(user);
	}
}
