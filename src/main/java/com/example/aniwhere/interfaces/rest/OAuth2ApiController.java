package com.example.aniwhere.interfaces.rest;

import com.example.aniwhere.application.user.KakaoService;
import com.example.aniwhere.global.error.ErrorCode;
import com.example.aniwhere.global.error.exception.InvalidTokenException;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OAuth2ApiController {

	private final KakaoService kakaoService;

	@ApiOperation("카카오 소셜 로그인 계정에 대한 로그아웃")
	@PostMapping("/auth/kakao/logout")
	public ResponseEntity<String> logout(
			@RequestHeader("Authorization") String accessToken,
			@RequestHeader("Refresh-Token") String refreshToken) {
		log.info("로그아웃 호출");

		if (!accessToken.startsWith("Bearer ")) {
			throw new InvalidTokenException("유효하지 않은 토큰입니다.", ErrorCode.INVALID_TOKEN);
		}

		String token = accessToken.substring(7);
		kakaoService.kakaoLogout(token, refreshToken);
		return ResponseEntity.ok("로그아웃이 성공적으로 처리되었습니다.");
	}
}
