package com.EEITG3.Airbnb.users.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {
	
	@Id
	@Column(name = "customer_id")
	@GeneratedValue(strategy = GenerationType.UUID)
	private String customerId;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "avatar_url")
	private String avatarURL;
	
	@Column(name = "is_active")
	private boolean isActive = true;
	
	@Column(name = "created_at")
	private LocalDate createAt = LocalDate.now();

	public Customer() {}
	
	public Customer(String email, String password, String username, String phone) {
		super();
		this.email = email;
		this.password = password;
		this.username = username;
		this.phone = phone;
	}
	

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAvatarURL() {
		return avatarURL;
	}

	public void setAvatarURL(String avatarURL) {
		this.avatarURL = avatarURL;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDate getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDate createAt) {
		this.createAt = createAt;
	}

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", email=" + email + ", password=" + password + ", username="
				+ username + ", phone=" + phone + ", avatarURL=" + avatarURL + ", isActive=" + isActive + ", createAt="
				+ createAt + "]";
	}
}
