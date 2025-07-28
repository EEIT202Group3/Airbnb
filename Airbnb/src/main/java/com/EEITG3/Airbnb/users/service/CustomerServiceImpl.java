package com.EEITG3.Airbnb.users.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService {

	private CustomerRepository repo;
	
	@Autowired
	public CustomerServiceImpl (CustomerRepository repo) {
		this.repo = repo;
	}
	
	@Override
	public Customer customerLogin(String email) {
		return repo.findCustomerByEmail(email);
	}

	@Override
	public Customer customerSignup(Customer customer) {
		return repo.save(customer);
	}

	@Override
	public Customer customerUpdate(Customer customer) {
		return repo.save(customer);
	}

	@Override
	public Customer permission(String status, String customerId) {
		//先找到客戶
		Optional<Customer> temp = repo.findById(customerId);
		Customer customer = new Customer();
		if(temp.isPresent()) {
			customer = temp.get();
		}
		//看前端傳來的指令是什麼，執行對應動作
		switch (status){
		case "ACTIVE": {
			customer.setActive(true);
			break;
		}
		case "SUSPEND": {
			customer.setActive(false);
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + status);
		}
		//把更新後的狀態存回資料庫、回傳更新後的資料
		return repo.save(customer);
	}

	
}
