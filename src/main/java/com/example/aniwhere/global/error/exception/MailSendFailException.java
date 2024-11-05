package com.example.aniwhere.global.error.exception;

import com.example.aniwhere.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class MailSendFailException extends ServerException {

	public MailSendFailException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

	public MailSendFailException(ErrorCode errorCode) {
		super(errorCode);
	}

	public MailSendFailException(String message) {
		super(ErrorCode.MAIL_SEND_FAIL);
	}
}
