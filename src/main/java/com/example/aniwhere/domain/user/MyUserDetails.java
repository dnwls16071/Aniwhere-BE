package com.example.aniwhere.domain.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class MyUserDetails implements UserDetails, OAuth2User {

	private final User user;
	private Map<String, Object> attributes;

	// spring security 일반 로그인용
	public MyUserDetails(User user) {
		this.user = user;
	}

	// spring security OAuth2 로그인용
	public MyUserDetails(User user, Map<String, Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}

	// OAuth2 로그인용
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}


	@Override
	public String getName() {
		return user.getEmail();
	}


	// 일반 로그인용
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add(() -> user.getRole().name());
		return collection;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
