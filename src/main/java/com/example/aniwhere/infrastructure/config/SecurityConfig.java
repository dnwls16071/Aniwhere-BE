package com.example.aniwhere.infrastructure.config;

import com.example.aniwhere.application.user.MyUserDetailsService;
import com.example.aniwhere.application.user.PrincipalOAuthDetailsService;
import com.example.aniwhere.infrastructure.jwt.JwtTokenFilter;
import com.example.aniwhere.infrastructure.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

	private final TokenProvider tokenProvider;
	private final PrincipalOAuthDetailsService principalOAuthDetailsService;
	private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
	private final MyUserDetailsService myUserDetailsService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.sessionManagement(c ->
						c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
							 new AntPathRequestMatcher("/oauth2/authorization/kakao"),
							 new AntPathRequestMatcher("/api/v1/oauth2/**"),
							 new AntPathRequestMatcher("/login/**"),
							 new AntPathRequestMatcher("/api/login"),
							 new AntPathRequestMatcher("/api/token"),
							 new AntPathRequestMatcher("/api/auth/login"),
                             new AntPathRequestMatcher("/api/auth/signup"),
 							 new AntPathRequestMatcher("/swagger-ui/**"),
							 new AntPathRequestMatcher("/v3/api-docs/**"),
							 new AntPathRequestMatcher("/")
						).permitAll()
						.anyRequest().authenticated()
				)
				.oauth2Login(oauth2 -> oauth2
						.userInfoEndpoint(userInfo -> userInfo
								.userService(principalOAuthDetailsService))
						.successHandler(customOAuth2SuccessHandler))
				.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public JwtTokenFilter jwtAuthenticationFilter() {
		return new JwtTokenFilter(tokenProvider, myUserDetailsService);
	}

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
