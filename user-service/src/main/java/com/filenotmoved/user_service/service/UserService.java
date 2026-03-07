package com.filenotmoved.user_service.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.filenotmoved.user_service.dto.UserDto;
import com.filenotmoved.user_service.entity.User;
import com.filenotmoved.user_service.entity.Role;
import com.filenotmoved.user_service.enums.Status;
import com.filenotmoved.user_service.exception.custom.UserAlreadyExistsException;
import com.filenotmoved.user_service.mapper.GenericMapper;
import com.filenotmoved.user_service.repository.UserRepository;
import com.filenotmoved.user_service.util.AuthTokenAndPasswordUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	
	public Boolean registerAppUser(UserDto userDto) {
		if (userRepository.existsById(userDto.getPhone())) {
			log.error("App User with phone {} already exists in the system ", userDto.getPhone());
			throw new UserAlreadyExistsException("User already exists in the system");
		}
		final GenericMapper<UserDto, User> mapper = new GenericMapper<>(modelMapper,
				UserDto.class, User.class);
		final User user = mapper.dtoToEntity(userDto);
		user.setRoles(List.of(new Role("USER")));
		user.setInternalPassword(AuthTokenAndPasswordUtil.generatorPassword());
		user.setStatus(Status.ACTIVE);
		userRepository.save(user);
		//otpService.createOtpForAppUser(appUser);
		log.info("App User {} with phone is register in the system ", userDto.getDisplayName(),
				userDto.getPhone());
		return true;
	}
	
}