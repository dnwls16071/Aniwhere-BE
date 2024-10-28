package com.example.aniwhere.interfaces.rest;

import com.example.aniwhere.domain.token.dto.NewAccessTokenRequest;
import com.example.aniwhere.domain.token.dto.NewAccessTokenResponse;
import com.example.aniwhere.application.token.TokenService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TokenApiController {

	private final TokenService tokenService;

	@ApiOperation("만료된 액세스 토큰을 재발급받기 위한 API")
	@PostMapping("/token")
	public ResponseEntity<NewAccessTokenResponse> createNewAccessToken(@RequestBody NewAccessTokenRequest request) {

		String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());
		return ResponseEntity.ok(new NewAccessTokenResponse(newAccessToken));
	}
}
