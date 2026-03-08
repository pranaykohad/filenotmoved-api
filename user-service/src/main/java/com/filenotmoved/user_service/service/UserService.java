package com.filenotmoved.user_service.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.filenotmoved.user_service.dto.UserDto;
import com.filenotmoved.user_service.entity.User;
import com.filenotmoved.user_service.entity.Role;
import com.filenotmoved.user_service.enums.Status;
import com.filenotmoved.user_service.exception.custom.AccessDeniedException;
import com.filenotmoved.user_service.exception.custom.UserAlreadyExistsException;
import com.filenotmoved.user_service.exception.custom.UserNotFoundException;
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

	public Boolean registerUser(UserDto userDto) {
		if (userRepository.existsById(userDto.getPhone())) {
			log.error("User with phone {} already exists in the system ", userDto.getPhone());
			throw new UserAlreadyExistsException("User already exists in the system");
		}
		final GenericMapper<UserDto, User> mapper = new GenericMapper<>(modelMapper,
				UserDto.class, User.class);
		final User user = mapper.dtoToEntity(userDto);
		user.setRoles(List.of(new Role("USER")));
		user.setInternalPassword(AuthTokenAndPasswordUtil.generatorPassword());
		user.setStatus(Status.ACTIVE);
		userRepository.save(user);
		// otpService.createOtpForAppUser(appUser);
		log.info("User {} with phone is register in the system ", userDto.getDisplayName(),
				userDto.getPhone());
		return true;
	}

	public User authenticateAppUserByOtp(String phone, String otp) {
		final User appUser = userRepository.findByPhoneAndOtp(phone, otp);
		if (appUser == null) {
			throw new AccessDeniedException("Phone number or OTP is not correct");
		}
		return appUser;
	}

	public User getExistingAppUser(String phoneNumber) {
		final User existingAppUser = userRepository.findByPhone(phoneNumber);
		if (existingAppUser == null) {
			throw new UserNotFoundException("User doesnot exists in the system");
		}
		return existingAppUser;
	}

}