package com.EEITG3.Airbnb.users.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hosts")
public class Host {

	@Id
	@Column(name = "host_id")
	@GeneratedValue(strategy = GenerationType.UUID)
	private String hostId;
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
	@Column(name = "intro")
	private String intro;
	@Column(name = "is_active")
	private boolean isActive = true;
	@Column(name = "created_at")
	private LocalDateTime createAt = LocalDateTime.now();
	
	public Host() {}
	public Host(String email, String password, String username, String phone) {
		super();
		this.email = email;
		this.password = password;
		this.username = username;
		this.phone = phone;
	}

	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
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
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public LocalDateTime getCreateAt() {
		return createAt;
	}
	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}
	
	@Override
	public String toString() {
		return "Host [hostId=" + hostId + ", email=" + email + ", password=" + password + ", username=" + username
				+ ", phone=" + phone + ", avatarURL=" + avatarURL + ", intro=" + intro + ", isActive=" + isActive
				+ ", createAt=" + createAt + "]";
	}
}
