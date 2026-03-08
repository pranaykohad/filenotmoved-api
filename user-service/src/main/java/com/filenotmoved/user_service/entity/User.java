package com.filenotmoved.user_service.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.filenotmoved.user_service.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

	@lombok.Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "varchar(20) default 'ACTIVE'")
	private Status status = Status.ACTIVE;

	@lombok.Builder.Default
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_phone", referencedColumnName = "phone"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Role> roles = new ArrayList<>();

}