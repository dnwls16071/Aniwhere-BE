package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class LoginFailureException extends ServerException {

    public LoginFailureException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

    public LoginFailureException(ErrorCode errorCode) {
		super(errorCode);
	}

	public LoginFailureException() {
		super(ErrorCode.LOGIN_FAILURE);
	}
}
