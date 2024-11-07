package com.example.aniwhere.jwt;

import com.example.aniwhere.domain.user.Role;
import com.example.aniwhere.domain.user.Sex;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.infrastructure.jwt.JwtProperties;
import com.example.aniwhere.infrastructure.jwt.TokenProvider;
import com.example.aniwhere.infrastructure.persistence.UserRepository;
import com.example.aniwhere.application.token.TokenService;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenProviderTest {

	@Autowired
	private TokenService tokenService;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtProperties jwtProperties;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Test
	@DisplayName("리프레시 토큰으로 새로운 액세스 토큰을 발급한다")
	void createNewAccessToken() {
		// given
		User savedUser = userRepository.save(User.builder()
				.email("user@gmail.com")
				.password("test")
				.role(Role.ROLE_USER)
				.birthday("2000.01.02")
				.sex(Sex.male)
				.build());
		String refreshToken = tokenProvider.generateRefreshToken(savedUser);

		// when
		String newAccessToken = tokenService.createNewAccessToken(refreshToken);

		// then
		assertThat(newAccessToken).isNotNull();
		assertThat(tokenProvider.validateToken(newAccessToken)).isTrue();
		assertThat(tokenProvider.getUserId(newAccessToken)).isEqualTo(savedUser.getId());
	}

	@DisplayName(value = "서버 측에서 발급된 리프레시 토큰은 캐싱을 위해 레디스에 저장되어야 한다.")
	@Test
	void generateRefreshTokenAndStoredInRedis() {
		// given
		User savedUser = userRepository.save(User.builder()
				.email("user@gmail.com")
				.password("test")
				.role(Role.ROLE_USER)
				.birthday("2000.01.02")
				.sex(Sex.male)
				.build());

		// when
		String refreshToken = tokenProvider.generateRefreshToken(savedUser);
		String storedRefreshToken = redisTemplate.opsForValue()
				.get("RT:" + savedUser.getEmail());

		// then
		assertThat(storedRefreshToken).isEqualTo(refreshToken);
	}

	@DisplayName(value = "서버 측에서 액세스 토큰과 리프레시 토큰을 발급한다.")
	@Test
	void generateToken() {
		// given
		User savedUser = userRepository.save(User.builder()
				.email("user@gmail.com")
				.password("test")
				.role(Role.ROLE_USER)
				.birthday("2000.01.02")
				.sex(Sex.female)
				.build());
		String accessToken = tokenProvider.generateAccessToken(savedUser);

		// when
		Long userId = Jwts.parser()
				.setSigningKey(jwtProperties.getSecretKey())
				.parseClaimsJws(accessToken)
				.getBody()
				.get("id", Long.class);

		// then
		assertThat(userId).isEqualTo(savedUser.getId());
	}

	@DisplayName(value = "만료된 토큰인 경우 유효성 검증에서 실패한다.")
	@Test
	void validateToken_failure() {
		// given
		String token = JwtFactory.builder()
				.expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
				.build()
				.createToken(jwtProperties);

		// when
		boolean result = tokenProvider.validateToken(token);

		// then
		assertThat(result).isFalse();
	}

	@DisplayName(value = "유효한 토큰인 경우 유효성 검증에 성공한다.")
	@Test
	void validateToken_Success() {
		// given
		String token = JwtFactory.withDefaultValues().createToken(jwtProperties);

		// when
		boolean result = tokenProvider.validateToken(token);

		// then
		assertThat(result).isTrue();
	}

	@DisplayName(value = "토큰 기반으로 인증 정보를 가져올 수 있다.")
	@Test
	void getAuthentication() {
		// given
		String userEmail = "user@email.com";
		String token = JwtFactory.builder()
				.subject(userEmail)
				.build()
				.createToken(jwtProperties);

		// when
		Authentication authentication = tokenProvider.getAuthentication(token);

		// then
		assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
	}

	@DisplayName(value = "토큰으로 유저ID를 가져올 수 있다.")
	@Test
	void getUserId() {
		// given
		Long userId = 1L;
		String token = JwtFactory.builder()
				.claims(Map.of("id", userId))
				.build()
				.createToken(jwtProperties);

		// when
		Long userIdByToken = tokenProvider.getUserId(token);

		// then
		assertThat(userIdByToken).isEqualTo(userId);
	}
}
