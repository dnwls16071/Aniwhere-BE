package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidTokenException extends ServerException {

    public InvalidTokenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode);
    }

	public InvalidTokenException() {
		super(ErrorCode.INVALID_TOKEN);
	}
}
