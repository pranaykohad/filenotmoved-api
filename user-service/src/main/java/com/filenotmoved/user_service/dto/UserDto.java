package com.filenotmoved.user_service.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.filenotmoved.user_service.entity.Role;
import com.filenotmoved.user_service.enums.Status;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

	@Email(message = "This email ia not valid")
    private String email;

    // Do not expose internal password by default; use a separate DTO for credential operations if needed
    private String displayName;

    @NotNull(message = "Phone cannot be null")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    private String phone;

    private LocalDateTime otpTimeStamp;

    private String otp;

    @Builder.Default
    private List<Role> roles = new ArrayList<>();
    
	@JsonIgnore
	private String internalPassword;

	@Builder.Default
	private Status status = Status.ACTIVE;

}