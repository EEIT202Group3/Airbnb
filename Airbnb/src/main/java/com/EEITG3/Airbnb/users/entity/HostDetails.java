package com.EEITG3.Airbnb.users.entity;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class HostDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	private Host host;
	
	public HostDetails(Host host) {
		this.host = host;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority("ROLE_HOST"));
	}

	@Override
	public String getPassword() {
		return host.getPassword();
	}

	@Override
	public String getUsername() {
		return host.getEmail();
	}
	
	@Override
    public boolean isAccountNonExpired() {
        return true;
    }
	//可以透過後臺來停權使用者
	@Override
    public boolean isAccountNonLocked() {
        return host.isActive(); // 若設為 false 則視為鎖定
    }
	@Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return host.isActive();
    }
	
	
}
