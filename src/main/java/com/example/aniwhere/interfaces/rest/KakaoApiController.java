package com.example.aniwhere.interfaces.rest;

import com.example.aniwhere.application.user.KakaoService;
import com.example.aniwhere.domain.user.dto.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class KakaoApiController {

	private final KakaoService kakaoService;

	@ApiOperation("카카오 로그인 요청 시 사용되는 API")
	@PostMapping("/auth/kakao/callback")
	public ResponseEntity<UserDTO.KakaoUserInfo> kakaoCallback(@RequestParam(name = "authorizationCode") String code,
															   HttpServletResponse response) throws JsonProcessingException {

		UserDTO.KakaoUserInfo kakaoUserInfo = kakaoService.kakaoLogin(code, response);
		return ResponseEntity.ok(kakaoUserInfo);
	}
}
