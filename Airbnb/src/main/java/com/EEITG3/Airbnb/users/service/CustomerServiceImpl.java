package com.EEITG3.Airbnb.users.service;

import java.util.List;
import java.util.Map;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.jwt.EmailService;
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
	private EmailService emailService;
	
	@Autowired
	public CustomerServiceImpl(CustomerRepository repo, ObjectMapper objectMapper, JwtService jwtService,
			AuthenticationManager authManager, PasswordEncoder encoder, EmailService emailService) {
		super();
		this.repo = repo;
		this.objectMapper = objectMapper;
		this.jwtService = jwtService;
		this.authManager = authManager;
		this.encoder = encoder;
		this.emailService = emailService;
	}
	
	@Override
	public String customerLogin(LogInRequest request) {
		Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		//如果驗證沒通過，拋出Exception，驗證失敗
		if(!authentication.isAuthenticated()) {
			throw new BadCredentialsException("驗證失敗");
		}
		//如果找不到帳戶，拋出Exception，帳號不存在
		Optional<Customer> temp = repo.findCustomerByEmail(request.getEmail());
		if(!temp.isPresent()) {
			throw new BadCredentialsException("帳號不存在");
		}
		Customer customer = temp.get();
		//如果沒有驗證過，拋出Exception，請先完成驗證
		if(!customer.isVerified()) {
			throw new BadCredentialsException("請先完成驗證");
		}
		//上面的三個驗證都過了，產生JWT並回傳
		return jwtService.generateToken(customer.getEmail(), "ROLE_CUSTOMER");
	}

	@Override
	public void customerSignup(SignUpRequest request) {
		Optional<Customer> temp = repo.findCustomerByEmail(request.getEmail());
		//表示已經有註冊過了
		if(temp.isPresent()) {
			throw new IllegalArgumentException("已註冊");
		}
		//先對密碼進行加密
		String encodedPassword = encoder.encode(request.getPassword());
		//用request收到的資料建立新的entity
		Customer customer = new Customer(request.getEmail(),encodedPassword,request.getUsername(),request.getPhone());
		//產生驗證信用的Token
		String token = UUID.randomUUID().toString();
		//設定驗證相關資料
		customer.setVerificationToken(token);
		customer.setVerified(false);
		//存入資料庫
		repo.save(customer);
		//發送驗證信
		emailService.sendVerificationEmail(customer.getEmail(), token, customer.getUsername());
	}
	
	@Override
	public String verify(String token) {
		Optional<Customer> temp = repo.findCustomerByToken(token);
		
		//無法透過token找到客戶(驗證沒過)
		if(!temp.isPresent()) {
			throw new RuntimeException("無效的連結");
		}
		//驗證過了，把已驗證設為true其他資料清掉
		Customer customer = temp.get();	
		customer.setVerified(true);
		customer.setVerificationToken(null);
		repo.save(customer);
		return jwtService.generateToken(customer.getEmail(), "ROLE_CUSTOMER");
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
