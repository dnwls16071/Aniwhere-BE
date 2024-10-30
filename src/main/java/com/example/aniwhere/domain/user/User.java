package com.example.aniwhere.domain.user;

import com.example.aniwhere.global.common.Common;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "USERS")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Common {

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(name = "birthyear")
	private String birthyear;

	@Column(name = "birthday")
	private String birthday;

	@Column(name = "sex")
	@Enumerated(EnumType.STRING)
	private Sex sex;

	@Column(name = "provider")
	private String provider;

	@Column(name = "providerId")
	private String providerId;

	// 정보 업데이트
	public User updateUser(User user) {
		this.nickname = user.getNickname();
		this.email = user.getEmail();
		this.providerId = user.getProviderId();
		this.provider = user.getProvider();
		return this;
	}
}
