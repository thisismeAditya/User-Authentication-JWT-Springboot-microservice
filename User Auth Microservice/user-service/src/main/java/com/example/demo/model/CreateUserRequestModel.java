package com.example.demo.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateUserRequestModel {
	
	@NotBlank(message = "Username field is required to proceed!")
	@Size(min = 3, message = "Username must be atleast 3 chracters long!")
	private String username;
	
	@NotBlank(message = "Email field is required to proceed!")
	@Email
	private String email;
	
	@NotBlank(message = "Password field is mandetory to proceed")
	@Size(min = 8, max = 16, message = "Password must be 8 to 16 characters long!")
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
