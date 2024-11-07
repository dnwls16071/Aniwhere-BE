package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {

	private ErrorCode errorCode;

	public ServerException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public ServerException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
