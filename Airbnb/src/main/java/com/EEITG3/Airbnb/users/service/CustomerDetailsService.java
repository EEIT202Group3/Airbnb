package com.EEITG3.Airbnb.users.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.entity.CustomerDetails;
import com.EEITG3.Airbnb.users.repository.CustomerRepository;

@Service
public class CustomerDetailsService implements UserDetailsService {

	private CustomerRepository repo;
	
	@Autowired
	public CustomerDetailsService(CustomerRepository repo) {
		this.repo = repo;
	}
	
	//因為要用email做驗證，所以這邊傳入email
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Customer> temp = repo.findCustomerByEmail(email);
		if(!temp.isPresent()) {
			throw new UsernameNotFoundException("找不到使用者");
		}
		Customer customer = temp.get();
		return new CustomerDetails(customer);
	}

}
