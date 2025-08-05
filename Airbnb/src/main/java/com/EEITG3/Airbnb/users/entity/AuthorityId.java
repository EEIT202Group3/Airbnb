package com.EEITG3.Airbnb.users.entity;

import java.io.Serializable;
import java.util.Objects;

public class AuthorityId implements Serializable {

	private static final long serialVersionUID = 1L;
	private String admin;
	private String authority;
	
	public AuthorityId() {}

	public AuthorityId(String admin, String authority) {
		super();
		this.admin = admin;
		this.authority = authority;
	}

	@Override
	public int hashCode() {
		return Objects.hash(admin, authority);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthorityId other = (AuthorityId) obj;
		return Objects.equals(admin, other.admin) && Objects.equals(authority, other.authority);
	}
}
