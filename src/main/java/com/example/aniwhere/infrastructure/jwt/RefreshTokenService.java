package com.example.aniwhere.infrastructure.jwt;

import com.example.aniwhere.domain.token.RefreshToken;
import com.example.aniwhere.global.error.ErrorCode;
import com.example.aniwhere.global.error.exception.NotFoundRefreshTokenException;
import com.example.aniwhere.infrastructure.persistence.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;

	public RefreshToken findByRefreshToken(String refreshToken) {
		return refreshTokenRepository.findByRefreshToken(refreshToken)
				.orElseThrow(() -> new NotFoundRefreshTokenException("리프레시 토큰을 찾을 수 없습니다.", ErrorCode.NOT_FOUND_REFRESH_TOKEN));
	}
}