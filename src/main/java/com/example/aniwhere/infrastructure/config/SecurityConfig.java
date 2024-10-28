package com.example.aniwhere.infrastructure.config;

import com.example.aniwhere.infrastructure.jwt.JwtTokenFilter;
import com.example.aniwhere.infrastructure.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

	private final TokenProvider tokenProvider;

	public SecurityConfig(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
							 new AntPathRequestMatcher("/api/token"),
							 new AntPathRequestMatcher("/api/auth/login"),
                             new AntPathRequestMatcher("/api/auth/signup"),
							 new AntPathRequestMatcher("/api/auth/kakao/callback"),
							 new AntPathRequestMatcher("/oauth/**"),
							 new AntPathRequestMatcher("/swagger-ui/**"),	    // swagger 문서 1
							 new AntPathRequestMatcher("/v3/api-docs/**"),      // swagger 문서 2
							 new AntPathRequestMatcher("/")
						).permitAll()
						.anyRequest().authenticated()
				)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	public JwtTokenFilter jwtAuthenticationFilter() {
		return new JwtTokenFilter(tokenProvider);
	}

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
