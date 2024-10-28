package com.example.aniwhere.domain.token;

import com.example.aniwhere.global.common.Common;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends Common {

	@Column(name = "user_id", nullable = false, unique = true)
	private Long userId;

	@Column(name = "refresh_token", nullable = false)
	private String refreshToken;

	public RefreshToken(Long userId, String refreshToken) {
		this.userId = userId;
		this.refreshToken = refreshToken;
	}

	public RefreshToken update(String newRefreshToken) {
		this.refreshToken = newRefreshToken;
		return this;
	}
}
