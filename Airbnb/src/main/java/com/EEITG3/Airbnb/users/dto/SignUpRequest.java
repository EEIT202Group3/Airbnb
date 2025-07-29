package com.EEITG3.Airbnb.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SignUpRequest {

	@NotBlank(message = "名字不可為空")
	private String username;
	
	@Email(message = "格式不正確")
	@NotBlank(message = "信箱不可為空")
	private String email;
	
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-={}:\\\";'<>?,./]).{8,}$", message = "密碼格式不正確")
	@NotBlank(message = "密碼不可為空")
	private String password;
	/*	
	1.由數字、英文組成
	2.至少有一個特殊字元
	3.長度至少為8
	4.至少有一個大寫英文
	*/
	
	@Pattern(regexp = "^09\\d{8}$", message = "電話格式不正確")
	@NotBlank(message = "電話不可為空")
	private String phone;
	
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}	
}
