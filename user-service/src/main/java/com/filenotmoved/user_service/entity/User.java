package com.filenotmoved.user_service.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.filenotmoved.user_service.enums.Role;
import com.filenotmoved.user_service.enums.Status;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

	@Column(nullable = true, unique = true)
	private String email;

	@JsonIgnore
	@Column(name = "internal_password", nullable = false)
	private String internalPassword;

	private String displayName;

	@Id
	@Column(nullable = false, unique = true)
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
	private String phone;

	private LocalDateTime otpTimeStamp;

	private String otp;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "varchar(20) default 'ACTIVE'")
	@lombok.Builder.Default
	private Status status = Status.ACTIVE;

	@lombok.Builder.Default
	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_phone", referencedColumnName = "phone"))
	@Column(name = "role")
	private List<Role> roles = new ArrayList<>();

}