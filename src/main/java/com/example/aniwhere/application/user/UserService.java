package com.example.aniwhere.application.user;

import com.example.aniwhere.domain.user.dto.UserDTO;
import com.example.aniwhere.domain.token.dto.JwtToken;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.global.error.exception.DuplicateEmailException;
import com.example.aniwhere.global.error.ErrorCode;
import com.example.aniwhere.global.error.exception.InvalidTokenException;
import com.example.aniwhere.global.error.exception.LoginFailureException;
import com.example.aniwhere.global.error.exception.NotFoundUserException;
import com.example.aniwhere.infrastructure.jwt.JwtProperties;
import com.example.aniwhere.infrastructure.persistence.UserRepository;
import com.example.aniwhere.infrastructure.jwt.TokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RedisTemplate<String, String> redisTemplate;
	private final JwtProperties jwtProperties;
	private final TokenProvider tokenProvider;

	// 스프링 시큐리티 회원가입 시 사용되는 signup 메서드
	public User signup(UserDTO.UserSignUpRequest request) {
		log.info("서비스 계층 회원가입 로직 수행");

		Optional<User> user = userRepository.findByEmail(request.getEmail());

		if (user.isPresent()) {
			throw new DuplicateEmailException(user.get().getEmail(), ErrorCode.NICKNAME_DUPLICATION);
		}

		User newUser = User.builder()
				.nickname(request.getNickname())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.birthday(request.getBirthday())
				.birthyear(request.getBirthyear())
				.sex(request.getSex())
				.role(request.getRole())
				.build();
		User savedUser = userRepository.save(newUser);
		return savedUser;
	}

	// 스프링 시큐리티 일반 로그인 시 사용되는 signin 메서드
	public JwtToken signin(UserDTO.UserSignInRequest request) {
		log.info("서비스 계층 로그인 로직 수행");

		Optional<User> user = userRepository.findByEmail(request.getEmail());
		if (user.isEmpty()) {
			throw new NotFoundUserException("해당 유저는 존재하지 않는 유저입니다.", ErrorCode.NOT_FOUND_USER);
		}

		User getUser = user.get();
		if (!passwordEncoder.matches(request.getPassword(), getUser.getPassword())) {
			throw new LoginFailureException("비밀번호가 일치하지 않습니다.", ErrorCode.LOGIN_FAILURE);
		}

		String accessToken = tokenProvider.generateAccessToken(getUser);
		String refreshToken = tokenProvider.generateRefreshToken(getUser);

		return JwtToken.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	// 스프링 시큐리티 로그아웃 시 사용되는 logout 메서드
	public void logout(JwtToken jwtToken) {
		String accessToken = jwtToken.getAccessToken();
		String refreshToken = jwtToken.getRefreshToken();

		if (!tokenProvider.validateToken(accessToken)) {
			throw new InvalidTokenException("유효하지 않은 토큰입니다.", ErrorCode.INVALID_TOKEN);
		}

		Claims claims = tokenProvider.getClaims(accessToken);
		String email = claims.getSubject();

		redisTemplate.opsForValue().set(
				"BAT: " + email,
				accessToken,
				jwtProperties.getAccess_token_expiration_time(),
				TimeUnit.MILLISECONDS
		);

		redisTemplate.opsForValue().set(
				"BRT: " + email,
				refreshToken,
				jwtProperties.getRefresh_token_expiration_time(),
				TimeUnit.MILLISECONDS
		);

		redisTemplate.delete("RT: " + email);
	}

	public User findById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundUserException("해당 유저는 존재하지 않는 유저입니다.", ErrorCode.NOT_FOUND_USER));
	}
}
