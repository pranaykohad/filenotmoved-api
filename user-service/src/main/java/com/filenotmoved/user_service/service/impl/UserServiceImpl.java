package com.filenotmoved.user_service.service.impl;

import org.springframework.stereotype.Service;

import com.filenotmoved.user_service.repository.UserRepository;
import com.filenotmoved.user_service.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

}