package com.EEITG3.Airbnb.users.entity;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomerDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	private Customer customer;
	
	public CustomerDetails(Customer customer) {
		this.customer = customer;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
	}
	@Override
	public String getPassword() {
		return customer.getPassword();
	}
	//因為要用email驗證，所以這邊調整一下
	@Override
	public String getUsername() {
		return customer.getEmail();
	}
	@Override
    public boolean isAccountNonExpired() {
        return true;
    }
	//可以透過後臺來停權使用者
	@Override
    public boolean isAccountNonLocked() {
        return customer.isActive(); // 若設為 false 則視為鎖定
    }
	@Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return customer.isActive();
    }
}
