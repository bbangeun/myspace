package com.example.demo.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.Authority;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.SecurityUtil;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional
	public User signup(UserDto userDto) {
		if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
			throw new RuntimeException("이미 가입되어 있는 유저입니다.");
		}
		
		Authority authority = Authority.builder()
				.authorityName("ROLE_USER")
				.build();
		
		User user = User.builder()
				.username(userDto.getUsername())
				.password(passwordEncoder.encode(userDto.getPassword()))
				.nickname(userDto.getNickname())
				.authorities(Collections.singleton(authority))
				.activated(true)
				.build();
		
		return userRepository.save(user);
	}
	
	// username을 파라미터로 받아 해당 유저의 정보 및 권한 정보를 리턴
	@Transactional(readOnly = true)
	public Optional<User> getUserWithAuthorities(String username) {
		return userRepository.findOneWithAuthoritiesByUsername(username);
	}
	
	// 위에서 만든 SecurityUtil의 getCurrentUsername() 메소드가 리턴하는 username의 유저 및 권한 정보 리턴
	@Transactional(readOnly = true)
	public Optional<User> gerMyUserWithAuthorities() {
		return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
	}
}
