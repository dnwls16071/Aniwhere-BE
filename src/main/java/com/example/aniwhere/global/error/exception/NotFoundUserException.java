package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundUserException extends ServerException {

    public NotFoundUserException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

    public NotFoundUserException(ErrorCode errorCode) {
		super(errorCode);
	}

    public NotFoundUserException() {
		super(ErrorCode.NOT_FOUND_USER);
	}
}
