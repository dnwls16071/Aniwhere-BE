package com.example.aniwhere.application.user;

import com.example.aniwhere.global.error.ErrorCode;
import com.example.aniwhere.global.error.exception.MailSendFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender emailSender;

	public void sendEmail(String toEmail, String title, String text) {

		SimpleMailMessage emailForm = createEmailForm(toEmail, title, text);
		try {
			emailSender.send(emailForm);
		} catch (RuntimeException e) {
			log.error("메일 발송에 실패했습니다. 발송 이메일:{}, 제목:{}, 내용:{}", toEmail, title, text);
			throw new MailSendFailException("메일 발송에 실패했습니다.", ErrorCode.MAIL_SEND_FAIL);
		}
 	}

	private SimpleMailMessage createEmailForm(String toEmail, String title, String text) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(toEmail);
		simpleMailMessage.setSubject(title);
		simpleMailMessage.setText("2차 인증 번호는 " + text + "입니다.");
		return simpleMailMessage;
	}
}
