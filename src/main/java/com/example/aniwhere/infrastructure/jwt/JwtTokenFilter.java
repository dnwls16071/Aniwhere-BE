package com.example.aniwhere.infrastructure.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

	private final static List<String> WHITE_LIST = List.of(
			"/api/auth/login",
			"/api/auth/signup",
			"/error",
			"/favicon.ico",
			"/"
	);
	private final TokenProvider tokenProvider;
	private final static String HEADER_AUTHORIZATION = "Authorization";
	private final static String TOKEN_PREFIX = "Bearer ";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		log.info("요청 URI={}", requestURI);

		if (WHITE_LIST.contains(requestURI)) {
			filterChain.doFilter(request, response);
			return;
		}

		String header = request.getHeader(HEADER_AUTHORIZATION);
		log.info("헤더 정보={}", header);

		if (header != null && header.startsWith(TOKEN_PREFIX)) {
			String token = header.substring(7);
			log.info("액세스 토큰={}", token);

			if (tokenProvider.validateToken(token)) {
				String email = tokenProvider.getEmail(token);
				log.info("현재 사용자 메일={}", email);

				// 인증 객체 정보 저장
				if (email != null) {
					Authentication authentication = tokenProvider.getAuthentication(token);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}

		filterChain.doFilter(request, response);
	}
}