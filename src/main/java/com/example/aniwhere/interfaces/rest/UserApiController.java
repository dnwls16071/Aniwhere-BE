package com.example.aniwhere.interfaces.rest;

import com.example.aniwhere.domain.user.dto.UserDTO;
import com.example.aniwhere.domain.token.dto.JwtToken;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.application.user.UserService;
import com.example.aniwhere.infrastructure.jwt.TokenProvider;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserApiController {

	private final UserService userService;
	private final TokenProvider tokenProvider;

	@ApiOperation("Aniwhere 자체 서비스 회원가입 REST API")
	@PostMapping("/auth/signup")
	public ResponseEntity<UserDTO.UserSignUpResponse> signup(@Valid @RequestBody UserDTO.UserSignUpRequest request) {
		log.info("회원가입 요청");

		User user = userService.signup(request);
		return ResponseEntity.ok(new UserDTO.UserSignUpResponse(user));
	}

	@ApiOperation("Aniwhere 자체 서비스 로그인 REST API")
	@PostMapping("/auth/login")
	public ResponseEntity<Void> login(@Valid @RequestBody UserDTO.UserSignInRequest request) {
		log.info("로그인 요청");

		JwtToken jwtToken = userService.signin(request);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + jwtToken.getAccessToken());
		headers.add("Refresh-Token", jwtToken.getRefreshToken());

		return ResponseEntity.ok()
				.headers(headers)
				.build();
	}

	@ApiOperation("Aniwhere 자체 서비스 로그아웃 REST API")
	@PostMapping("/auth/logout")
	public ResponseEntity<String> logout(HttpServletRequest request) {
		log.info("로그아웃 요청");

		JwtToken jwtToken = tokenProvider.resolveToken(request);
		userService.logout(jwtToken);

		return ResponseEntity.ok().body("성공적으로 로그아웃되었습니다.");
	}
}
