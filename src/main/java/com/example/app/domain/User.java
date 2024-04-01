package com.example.app.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	private Integer userId;
	private String userName;
	@NotBlank
	private String userLogin;
	@NotBlank
	private String userPassword;
}
