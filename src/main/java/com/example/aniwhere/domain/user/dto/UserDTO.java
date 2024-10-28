package com.example.aniwhere.domain.user.dto;

import com.example.aniwhere.domain.user.Role;
import com.example.aniwhere.domain.user.Sex;
import com.example.aniwhere.domain.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

public class UserDTO {

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	@Builder
	public static class UserSignUpRequest {

		@NotEmpty(message = "닉네임은 필수입니다.")
		private String nickname;

		@Email(message = "이메일 형식이 올바르지 않습니다.")
		private String email;

		@NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
		private String password;

		@NotEmpty(message = "출생연도를 입력해주세요. Ex) 1995")
		private String birthyear;

		@NotEmpty(message = "출생일자를 입력해주세요. Ex) 0101")
		private String birthday;

		@ValidEnum(enumClass = Sex.class)
		private Sex sex;
		private final Role role = Role.ROLE_USER;
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	@Builder
	public static class UserSignUpResponse {
		private Long id;
		private String nickname;
		private String email;
		private String birthday;
		private String birthyear;
		private Sex sex;
		private Role role;

		public UserSignUpResponse(User user) {
			this.id = user.getId();
			this.email = user.getEmail();
			this.nickname = user.getNickname();
			this.birthday = user.getBirthday();
			this.birthyear = user.getBirthyear();
			this.sex = user.getSex();
			this.role = user.getRole();
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	public static class UserSignInRequest {

		@Email(message = "이메일 형식이 올바르지 않습니다.")
		private String email;

		@NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
		private String password;
	}

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	public static class KakaoUserInfo {
		private Long id;
		private String email;		// 이메일
		private String nickname;	// 별명
		private String name;		// 이름
		private String gender;		// 성별 ex) male, female
		private String birthday;	// 월일 ex) 0101
		private String birthyear;	// 생년 ex) 2000
		private String profile_image_url;	// 프로필 이미지
	}
}