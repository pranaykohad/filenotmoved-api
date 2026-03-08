package com.filenotmoved.user_service.controller;

import java.security.Principal;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.filenotmoved.user_service.dto.UserDto;
import com.filenotmoved.user_service.dto.UserLoginReqDto;
import com.filenotmoved.user_service.entity.User;
import com.filenotmoved.user_service.exception.custom.UnauthorizedException;
import com.filenotmoved.user_service.mapper.GenericMapper;
import com.filenotmoved.user_service.service.JwtService;
import com.filenotmoved.user_service.service.UserService;
import com.filenotmoved.user_service.util.JwtHelper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;

    @PostMapping("register")
    public ResponseEntity<Boolean> register(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.registerUser(userDto));
    }

    @PostMapping("login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginReqDto userLoginReqDto,
            Principal principal) {

        // Authenticate user by phone and OTP
        userService.authenticateAppUserByOtp(userLoginReqDto.getPhone(), userLoginReqDto.getOtp());

        // Check is user exists and extract List<authority>
        final User existingAppUser = userService.getExistingAppUser(userLoginReqDto.getPhone());

        // Create, save and add JWT token in response
        final String tokenForAppUser = jwtService.generateTokenForAppUser(userLoginReqDto.getPhone(),
                JwtHelper.rolesToAuthorities(existingAppUser.getRoles()));
        final GenericMapper<UserDto, User> mapper = new GenericMapper<>(modelMapper, UserDto.class,
                User.class);
        final UserDto apppUserResponseDto = mapper.entityToDto(existingAppUser);
        apppUserResponseDto.setJwt(tokenForAppUser);
        return ResponseEntity.ok(apppUserResponseDto);
    }

    @GetMapping("loggedin-user-info")
    public ResponseEntity<UserDto> getLoggedinAppUserInfo(Principal principal) {
        if (principal != null) {
            final GenericMapper<UserDto, User> mapper = new GenericMapper<>(modelMapper,
                    UserDto.class, User.class);
            final User userActive = userService.getExistingAppUser(principal.getName());
            return ResponseEntity.ok(mapper.entityToDto(userActive));
        }
        throw new UnauthorizedException("Unauthorized access");
    }

}