package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicateEmailException extends BusinessException {

	public DuplicateEmailException(String email, ErrorCode errorCode) {
		super(email, errorCode);
	}

	public DuplicateEmailException(ErrorCode errorCode) {
		super(errorCode);
	}

	public DuplicateEmailException(String email) {
		super(email, ErrorCode.NICKNAME_DUPLICATION);
	}
}
