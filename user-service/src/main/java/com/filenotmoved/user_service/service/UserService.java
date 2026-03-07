package com.filenotmoved.user_service.service;

import org.springframework.stereotype.Service;

import com.filenotmoved.user_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	
	private final UserRepository userRepository;
}