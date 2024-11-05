package com.example.aniwhere.interfaces.rest;

import com.example.aniwhere.application.user.UserService;
import com.example.aniwhere.domain.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MailApiController {

	private final UserService userService;

	@PostMapping("/auth/email/verifications-requests")
	public ResponseEntity sendMessage(@RequestParam(name = "email") String email) {
		userService.sendCodeToEmail(email);
		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("/auth/email/verifications")
	public ResponseEntity<UserDTO.EmailVerificationResponse> verificationEmail(@RequestParam(name = "email") String email,
																			   @RequestParam(name = "code") String code) {
		UserDTO.EmailVerificationResponse emailVerificationResponse = userService.verifiedCode(email, code);
		return ResponseEntity.ok(emailVerificationResponse);
	}
}
