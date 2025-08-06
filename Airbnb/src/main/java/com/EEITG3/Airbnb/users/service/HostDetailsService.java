package com.EEITG3.Airbnb.users.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.users.entity.Host;
import com.EEITG3.Airbnb.users.entity.HostDetails;
import com.EEITG3.Airbnb.users.repository.HostRepository;

@Service
public class HostDetailsService implements UserDetailsService {

	private HostRepository repo;
	
	@Autowired
	public HostDetailsService(HostRepository repo) {
		this.repo = repo;
	}
	
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Host> temp = repo.findHostByEmail(email);
		if(!temp.isPresent()) {
			throw new UsernameNotFoundException("找不到使用者");
		}
		Host host = temp.get();
		return new HostDetails(host);
	}

}
