package com.EEITG3.Airbnb.users.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "authorities")
@IdClass(AuthorityId.class) //因為採用複合主鍵，所以需要指定IdClass
public class Authority {

	@Id
	@ManyToOne
	@JoinColumn(name = "admin_id")
	@JsonBackReference
	private Admin admin;
	
	@Id
	@Column(name = "authority")
	private String authority;
	
	public Admin getAdmin() {
		return admin;
	}
	public void setAdmin(Admin admin) {
		this.admin = admin;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}

}
