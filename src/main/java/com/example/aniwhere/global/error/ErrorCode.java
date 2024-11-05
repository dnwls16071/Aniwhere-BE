package com.example.aniwhere.global.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ErrorCode {

	INVALID_INPUT_VALUE(400, "C001", "유효하지 않은 입력값입니다."),
	METHOD_NOT_ALLOWED(405, "C002", "허용되지 않는 HTTP 메서드입니다."),
	INVALID_TYPE_VALUE(400, "C004", "유효하지 않은 타입입니다."),
	HANDLE_ACCESS_DENIED(403, "C005", "접근 권한이 없습니다."),
	NICKNAME_DUPLICATION(400, "C006", "중복된 닉네임입니다."),
	EMAIL_DUPLICATION(400, "C007", "중복된 메일입니다."),
	NOT_FOUND_USER(400, "C008", "사용자를 찾을 수 없습니다."),
	LOGIN_FAILURE(400, "C009", "이메일은 맞지만 패스워드가 일치하지 않습니다."),
	INVALID_TOKEN(400, "C010", "유효하지 않은 토큰입니다."),
	UNAUTHORIZED(401, "C011", "권한이 없습니다."),

	INTERNAL_SERVER_ERROR(500, "S001", "서버 측 에러입니다."),
	NOT_FOUND_REFRESH_TOKEN(500, "S002", "리프레시 토큰을 찾을 수 없습니다."),
	MAIL_SEND_FAIL(500, "S003", "메일 전송에 실패했습니다.");

	private int status;
	private final String code;
	private final String message;

	ErrorCode(final int status, final String code, final String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
