package com.filenotmoved.user_service.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.filenotmoved.user_service.dto.UserDto;
import com.filenotmoved.user_service.entity.User;
import com.filenotmoved.user_service.mapper.GenericMapper;
import com.filenotmoved.user_service.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/user-data")
@AllArgsConstructor
public class UserDataController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        final GenericMapper<UserDto, User> mapper = new GenericMapper<>(modelMapper, UserDto.class, User.class);
        final List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(mapper.entityToDto(users));
    }

}
