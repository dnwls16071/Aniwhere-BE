package com.example.aniwhere.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

	@Value("${spring.mail.smtp.host}")
	private String host;

	@Value("${spring.mail.smtp.port}")
	private int port;

	@Value("${spring.mail.smtp.username}")
	private String username;

	@Value("${spring.mail.smtp.password}")
	private String password;

	@Value("${spring.mail.smtp.properties.mail.auth}")
	private boolean auth;

	@Value("${spring.mail.smtp.properties.mail.starttls.enable}")
	private boolean starttlsEnable;

	@Value("${spring.mail.smtp.properties.mail.starttls.required}")
	private boolean starttlsRequired;

	@Value("${spring.mail.smtp.properties.mail.connectiontimeout}")
	private int connectionTimeout;

	@Value("${spring.mail.smtp.properties.mail.timeout}")
	private int timeout;

	@Value("${spring.mail.smtp.properties.mail.writetimeout}")
	private int writeTimeout;

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(host);
		javaMailSender.setPort(port);
		javaMailSender.setUsername(username);
		javaMailSender.setPassword(password);
		javaMailSender.setDefaultEncoding("UTF-8");
		javaMailSender.setJavaMailProperties(getMailProperties());
		return javaMailSender;
	}

	private Properties getMailProperties() {
		Properties properties = new Properties();
		properties.put("mail.smtp.starttls.enable", starttlsEnable);
		properties.put("mail.smtp.starttls.required", starttlsRequired);
		properties.put("mail.smtp.connectiontimeout", connectionTimeout);
		properties.put("mail.smtp.timeout", timeout);
		properties.put("mail.smtp.writetimeout", writeTimeout);
		properties.put("mail.smtp.auth", auth);
		return properties;
	}
}
