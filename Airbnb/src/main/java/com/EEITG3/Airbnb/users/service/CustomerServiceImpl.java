package com.EEITG3.Airbnb.users.service;

import java.util.List;
import java.util.Map;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.jwt.JwtService;
import com.EEITG3.Airbnb.users.dto.LogInRequest;
import com.EEITG3.Airbnb.users.dto.SignUpRequest;
import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.entity.CustomerDetails;
import com.EEITG3.Airbnb.users.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class CustomerServiceImpl implements CustomerService {

	private CustomerRepository repo;
	private ObjectMapper objectMapper;
	private JwtService jwtService;
	private AuthenticationManager authManager;
	private PasswordEncoder encoder;
	
	@Autowired
	public CustomerServiceImpl(CustomerRepository repo, ObjectMapper objectMapper, JwtService jwtService,
			AuthenticationManager authManager, PasswordEncoder encoder) {
		super();
		this.repo = repo;
		this.objectMapper = objectMapper;
		this.jwtService = jwtService;
		this.authManager = authManager;
		this.encoder = encoder;
	}
	
	@Override
	public String customerLogin(LogInRequest request) {
		Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		if(authentication.isAuthenticated()) {
			return jwtService.generateToken(request.getEmail(),"ROLE_CUSTOMER");
		} else {
			throw new BadCredentialsException("驗證失敗");
		}
	}

	@Override
	public String customerSignup(SignUpRequest request) {
		Optional<Customer> temp = repo.findCustomerByEmail(request.getEmail());
		//表示已經有註冊過了
		if(temp.isPresent()) {
			throw new IllegalArgumentException("已註冊");
		}
		//先對密碼進行加密
		String encodedPassword = encoder.encode(request.getPassword());
		//用request收到的資料建立新的entity
		Customer customer = new Customer(request.getEmail(),encodedPassword,request.getUsername(),request.getPhone());
		//存入資料庫
		repo.save(customer);
		//用這個使用者生成token
		return jwtService.generateToken(customer.getEmail(),"ROLE_CUSTOMER");
	}

	@Override
	public Customer customerUpdate(Map<String, Object> patchPayload, CustomerDetails customerDetails) {
		Optional<Customer> temp = repo.findCustomerByEmail(customerDetails.getUsername());
		if(!temp.isPresent()) {
			throw new RuntimeException("找不到客戶");
		}
		Customer customer = temp.get();
		Customer updatedCustomer = apply(patchPayload, customer);
		return repo.save(updatedCustomer);
	}
	private Customer apply(Map<String, Object> patchPayload, Customer customer) {
		ObjectNode customerNode = objectMapper.convertValue(customer, ObjectNode.class);
		ObjectNode patchNode = objectMapper.convertValue(patchPayload, ObjectNode.class);
		customerNode.setAll(patchNode);
		return objectMapper.convertValue(customerNode, Customer.class);
	}
	
	@Override
	public Customer permission(String status, String customerEmail) {
		//先找到客戶
		Optional<Customer> temp = repo.findCustomerByEmail(customerEmail);
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

	@Override
	public List<Customer> findAllCustomers() {
		return repo.findAll();
	}

	@Override
	public Customer currentCustomer(CustomerDetails customerDetails) {
		Optional<Customer> temp = repo.findCustomerByEmail(customerDetails.getUsername());
		if(!temp.isPresent()) {
			throw new RuntimeException("找不到客戶");
		}
		Customer customer = temp.get();
		return customer;
	}

	
}
