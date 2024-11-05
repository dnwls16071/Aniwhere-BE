package com.example.aniwhere.global.error;

import com.example.aniwhere.global.error.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.error("handleMethodArgumentNotValidException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	// 메일 중복 예외
	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException e) {
		log.error("handleDuplicateEmailException", e);
		final ErrorResponse response = ErrorResponse.of(e.getErrorCode());
		return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
	}

	// 로그인 실패 예외
	@ExceptionHandler(LoginFailureException.class)
	public ResponseEntity<ErrorResponse> handleLoginFailureException(LoginFailureException e) {
		log.error("handleLoginFailureException", e);
		final ErrorResponse response = ErrorResponse.of(e.getErrorCode());
		return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
	}

	// 리프레시 토큰 예외
	@ExceptionHandler(NotFoundRefreshTokenException.class)
	public ResponseEntity<ErrorResponse> handleNotFoundRefreshTokenException(NotFoundRefreshTokenException e) {
		log.error("handleNotFoundRefreshTokenException", e);
		final ErrorResponse response = ErrorResponse.of(e.getErrorCode());
		return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
	}

	// 유저 예외
	@ExceptionHandler(NotFoundUserException.class)
	public ResponseEntity<ErrorResponse> handleNotFoundUserException(NotFoundUserException e) {
		log.error("handleNotFoundUserException", e);
		final ErrorResponse response = ErrorResponse.of(e.getErrorCode());
		return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException e) {
		log.error("handleBusinessException", e);
		final ErrorResponse response = ErrorResponse.of(e.getErrorCode());
		return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
	}

	@ExceptionHandler(ServerException.class)
	public ResponseEntity<ErrorResponse> handleServerException(final ServerException e) {
		log.error("handleServerException", e);
		final ErrorResponse response = ErrorResponse.of(e.getErrorCode());
		return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("handleException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
