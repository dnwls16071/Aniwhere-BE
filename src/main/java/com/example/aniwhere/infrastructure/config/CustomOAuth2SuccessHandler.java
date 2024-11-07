package com.example.aniwhere.infrastructure.config;

import com.example.aniwhere.application.cache.RedisService;
import com.example.aniwhere.domain.user.MyUserDetails;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.infrastructure.jwt.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final RedisService redisService;
	private final TokenProvider tokenProvider;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		log.info("OAuth2 Login 성공");
		log.info("SecurityContextHolder.getContext().getAuthentication()={}", SecurityContextHolder.getContext().getAuthentication());
		MyUserDetails details = (MyUserDetails) authentication.getPrincipal();
		User user = details.getUser();

		// OAuth2 서버로부터 발급받는 사용자 액세스 토큰
		String oAuthAccessToken = details.getOAuthAccessToken();
		redisService.saveOAuthToken(user.getEmail(), oAuthAccessToken);

		// 백엔드에서 발급해주는 JWT 토큰
		String accessToken = tokenProvider.generateAccessToken(user);
		String refreshToken = tokenProvider.generateRefreshToken(user);

		response.setHeader("Authorization", "Bearer " + accessToken);
		response.setHeader("Refresh-Token", refreshToken);
	}
}
