package com.example.aniwhere.interfaces.rest;

import com.example.aniwhere.domain.token.dto.NewAccessTokenResponse;
import com.example.aniwhere.application.token.TokenService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TokenApiController {

	private final TokenService tokenService;

	@ApiOperation("만료된 액세스 토큰을 재발급받기 위한 API")
	@PostMapping("/reissue")
	public ResponseEntity<NewAccessTokenResponse> createNewAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
		log.info("토큰 재발급 요청");

		String newAccessToken = tokenService.createNewAccessToken(refreshToken);
		return ResponseEntity.ok(new NewAccessTokenResponse(newAccessToken, "토큰 재발급에 성공하셨습니다."));
	}
}
