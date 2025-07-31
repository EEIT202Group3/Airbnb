package com.EEITG3.Airbnb.users.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.users.CookieUtil;
import com.EEITG3.Airbnb.users.dto.LogInRequest;
import com.EEITG3.Airbnb.users.dto.SignUpRequest;
import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.entity.CustomerDetails;
import com.EEITG3.Airbnb.users.service.CustomerService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CustomerController {

	private CustomerService service;
	
	@Autowired
	public CustomerController(CustomerService service) {
		this.service = service;
	}
	
	//客戶登入
	@PostMapping("/customers/login")
	public ResponseEntity<?> logIn(@RequestBody LogInRequest request, HttpServletResponse response) {
		try {
			String token = service.customerLogin(request);
			CookieUtil.saveCookie(response, token);
			return ResponseEntity.ok(token);
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}
	
	//客戶註冊
	@PostMapping("/customers/signup")
	public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request,BindingResult bindingResult, HttpServletResponse response) {
		if(bindingResult.hasErrors()) {
			StringBuilder errorMessage = new StringBuilder();
			List<FieldError> errors = bindingResult.getFieldErrors();
			for(FieldError error : errors) {
				errorMessage.append(error.getField())
							.append(":")
							.append(error.getDefaultMessage())
							.append(";");
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
		String token = service.customerSignup(request);
		CookieUtil.saveCookie(response, token);
		return ResponseEntity.status(HttpStatus.CREATED).body(token);
	}
	
	//客戶更新資料
	@PatchMapping("/customers/update")
	public ResponseEntity<?> update(@RequestBody Map<String, Object> patchPayload) {
		try {
			Customer customer = service.customerUpdate(patchPayload);
			return ResponseEntity.ok(customer);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
	
	//找全部客戶資料
	@GetMapping("/customers")
	public List<Customer> getAllCustomers(){
		return service.findAllCustomers();
	}
	
	//找目前客戶的資料
	@GetMapping("/customers/current")
	public Customer getCurrentCustomer(@AuthenticationPrincipal CustomerDetails customerDetails) {
		return service.currentCustomer(customerDetails);
	}
	

	
}
