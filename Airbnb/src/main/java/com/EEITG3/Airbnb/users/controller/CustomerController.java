package com.EEITG3.Airbnb.users.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
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
	
//前台部分
	//客戶登入
	@PostMapping("/customers/login")
	public ResponseEntity<?> logIn(@RequestBody LogInRequest request, HttpServletResponse response) {
		try {
			String token = service.customerLogin(request);
			CookieUtil.saveCustomerCookie(response, token);
			return ResponseEntity.ok(token);
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}
	
	//客戶註冊
	@PostMapping("/customers/signup")
	public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request,BindingResult bindingResult) {
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
		service.customerSignup(request);
		return ResponseEntity.ok("已送出驗證信");
	}
	
	//接收驗證信
	@GetMapping("/customers/verify")
	public ResponseEntity<?> verify(@RequestParam("token") String token, HttpServletResponse response){
		String jwt = service.verify(token);
		CookieUtil.saveCustomerCookie(response, jwt);
		return ResponseEntity.ok("驗證成功");
	}
	
	//客戶更新資料
	@PatchMapping("/customers/update")
	public ResponseEntity<?> update(@RequestBody Map<String, Object> patchPayload, @AuthenticationPrincipal CustomerDetails customerDetails) {
		try {
			Customer customer = service.customerUpdate(patchPayload, customerDetails);
			return ResponseEntity.ok(customer);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
	
	//找個人資料
	@GetMapping("/customers/current")
	public Customer getCurrentCustomer(@AuthenticationPrincipal CustomerDetails customerDetails) {
		return service.currentCustomer(customerDetails);
	}
	
	
	
//後台部分
	//找全部客戶資料
	@GetMapping("/admins/customers")
	public List<Customer> getAllCustomers(){
		return service.findAllCustomers();
	}
	
	//更改權限
	@PatchMapping("/admins/customers/updatePermission")
	public ResponseEntity<?> updatePermission(@RequestBody Map<String, Object> data){
		try {
			String status = (String) data.get("status");
			String email = (String) data.get("email");
			service.permission(status, email);
			return ResponseEntity.ok(Map.of("message","success"));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	//模糊搜尋
	@GetMapping("/admins/customers/findlike")
	public ResponseEntity<?> findCustomerLike(@RequestParam String keyword, @RequestParam String context){
		List<Customer> customers = new ArrayList<Customer>();
		switch (keyword) {
			case("email"):{
				customers = service.findLikeByEmail(context);
				break;
			}
			case("username"):{
				customers = service.findLikeByUsername(context);
				break;
			}
			case("phone"):{
				customers = service.findLikeByPhone(context);
				break;
			}
			default:{
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("\"message\":\"keyword輸入錯誤\"");
			}
		}
		if(customers.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("\"message\":\"找不到客戶\"");
		}
		return ResponseEntity.ok(customers);
	}
}














