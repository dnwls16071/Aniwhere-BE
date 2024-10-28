package com.example.aniwhere.application.user;

import com.example.aniwhere.domain.user.MyUserDetails;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email).
				orElseThrow(() -> new UsernameNotFoundException("해당 유저는 존재하지 않는 유저입니다."));
		return new MyUserDetails(user);
	}
}
