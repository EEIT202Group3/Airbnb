package com.EEITG3.Airbnb.users.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.EEITG3.Airbnb.users.CookieUtil;
import com.EEITG3.Airbnb.users.dto.LogInRequest;
import com.EEITG3.Airbnb.users.dto.SignUpRequest;
import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.entity.CustomerDetails;
import com.EEITG3.Airbnb.users.service.CustomerService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

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
	
	@Value("${google.oauth.client-id}")
	private String clientId;
	
	private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
	
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
	
	//使用google登入
	@PostMapping("/customers/google")
	public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, Object> data, HttpServletResponse response){
		
		//從 RequestBody 中取得 google id token
		String token = (String)data.get("token");
		
		System.out.println("收到資料"+token);
		
		//建立 google id token 的驗證器
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
																  .setAudience(Arrays.asList(clientId))
																  .build();
		try {
			//驗證 google id token
			GoogleIdToken googleIdToken = verifier.verify(token);
			if(googleIdToken==null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("無效的 Google Token");
			}
			//從 google id token 裡面取得使用者資料
			Payload payload = googleIdToken.getPayload();
			//呼叫service執行登入
			String jwt = service.loginWithGoogle(payload);
			//把收到的 jwt 存入 cookie
			if(!(jwt==null)) {
				CookieUtil.saveCustomerCookie(response, jwt);
				return ResponseEntity.ok(jwt);
			}else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("登入失敗");
			}
			
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Auth temporarily unavailable");
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Auth verification error");
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
		try {
			service.customerSignup(request);
			return ResponseEntity.ok("已送出驗證信");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}
	
	//接收驗證信
	@GetMapping("/customers/verify")
	public void verify(@RequestParam("token") String token, HttpServletResponse response){
		String jwt = service.verify(token);
		CookieUtil.saveCustomerCookie(response, jwt);
		try {
			response.sendRedirect("http://localhost:5173");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//客戶登出
	@PostMapping("/customers/logout")
	public ResponseEntity<?> logout(HttpServletResponse response){
		CookieUtil.deleteCustomerCookie(response);
		return ResponseEntity.ok("登出成功");
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
	//更新大頭照
	@PostMapping(value = "/customers/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateAvatar(@RequestPart MultipartFile avatar, @AuthenticationPrincipal CustomerDetails customerDetails) {
		try {
			Customer customer = service.updateAvatar(service.currentCustomer(customerDetails), avatar);
			return ResponseEntity.ok(customer);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	//忘記密碼
	@PostMapping("/customers/forgetpwd")
	public ResponseEntity<?> forgetpwd(@RequestBody Map<String, Object> data){
		try {
			//送驗證信給他
			String email = (String)data.get("email");
			System.out.println(email);
			service.forgetPwd(email);
			//回傳OK
			return ResponseEntity.ok("已送出驗證信");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("你還未註冊");
		}
	}
	
	@GetMapping("/customers/pwdverify")
	public void pwdVerify(@RequestParam("token") String token,HttpServletResponse response){
		String jwt = service.verify(token);
		CookieUtil.saveCustomerCookie(response, jwt);
		try {
			response.sendRedirect("http://localhost:5173/password");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			String adminId = MDC.get("userId");
			log.info("更改客戶權限，客戶：%s;操作者：%s".formatted(email,adminId));
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
		String adminId = MDC.get("userId");
		log.info("查詢客戶資料，操作者：%s".formatted(adminId));
		return ResponseEntity.ok(customers);
	}
	
	//查詢當月註冊人數
	@GetMapping("/getMonthlyRegistCustomers")
	public ResponseEntity<?> getMonthlyRegist(){
		Map<String, Object> result = service.getMonthlyRegist();
		return ResponseEntity.ok(result);
	}
}














