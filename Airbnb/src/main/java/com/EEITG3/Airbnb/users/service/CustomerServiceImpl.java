package com.EEITG3.Airbnb.users.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.users.dto.LogInRequest;
import com.EEITG3.Airbnb.users.dto.SignUpRequest;
import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class CustomerServiceImpl implements CustomerService {

	private CustomerRepository repo;
	private ObjectMapper objectMapper;
	
	@Autowired
	public CustomerServiceImpl (CustomerRepository repo, ObjectMapper objectMapper) {
		this.repo = repo;
		this.objectMapper = objectMapper;
	}
	
	@Override
	public Customer customerLogin(LogInRequest request) {
		//透過email找客戶
		Customer customer = repo.findCustomerByEmail(request.getEmail());
		//如果找不到客戶，丟出Exception
		if(customer==null) {
			throw new RuntimeException("帳號不存在");
		}
		//密碼不一樣，丟出Exception
		if(!request.getPassword().equals(customer.getPassword())) {
			throw new RuntimeException("帳號密碼錯誤");
		}
		//上面的檢查都沒問題，回傳客戶資料
		return customer;
	}

	@Override
	public Customer customerSignup(SignUpRequest request) {
		Customer customer = new Customer(request.getEmail(),request.getPassword(),request.getUsername(),request.getPhone());
		return repo.save(customer);
	}

	@Override
	public Customer customerUpdate(String customerId, Map<String, Object> patchPayload) {
		Optional<Customer> temp = repo.findById(customerId);
		if(!temp.isPresent()) {
			throw new RuntimeException("帳號不存在");
		}
		Customer customer = apply(patchPayload, temp.get());
		return repo.save(customer);
	}
	private Customer apply(Map<String, Object> patchPayload, Customer customer) {
		ObjectNode customerNode = objectMapper.convertValue(customer, ObjectNode.class);
		ObjectNode patchNode = objectMapper.convertValue(patchPayload, ObjectNode.class);
		customerNode.setAll(patchNode);
		return objectMapper.convertValue(customerNode, Customer.class);
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
