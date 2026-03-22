package com.filenotmoved.user_service.service;

import org.springframework.stereotype.Service;

import com.filenotmoved.user_service.entity.User;
import com.filenotmoved.user_service.enums.Status;
import com.filenotmoved.user_service.exception.custom.UserNotFoundException;
import com.filenotmoved.user_service.repository.UserRepository;
import com.filenotmoved.user_service.util.AuthTokenAndPasswordUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final UserRepository userRepository;

    public boolean createAndOtpOnDevice(final String phone) {
        User user = userRepository.findByPhoneAndStatus(phone, Status.ACTIVE);
        if (user == null) {
            throw new UserNotFoundException("User doesnot exists in the system");
        }
        String otp = AuthTokenAndPasswordUtil.generateAuthToken();
        user.setOtp(otp);
        userRepository.save(user);
        sendOtpToUser(phone, otp);
        return true;
    }

    public void sendOtpToUser(final String phone, final String otp) {
        // Implement MSG91 API to send OTP to user
    }
}
