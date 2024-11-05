package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundRefreshTokenException extends ServerException {

    public NotFoundRefreshTokenException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

    public NotFoundRefreshTokenException(ErrorCode errorCode) {
		super(errorCode);
	}

    public NotFoundRefreshTokenException() {
		super(ErrorCode.NOT_FOUND_REFRESH_TOKEN);
	}
}
