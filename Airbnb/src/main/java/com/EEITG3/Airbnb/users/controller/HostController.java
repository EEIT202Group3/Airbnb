package com.EEITG3.Airbnb.users.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.EEITG3.Airbnb.users.entity.Host;
import com.EEITG3.Airbnb.users.entity.HostDetails;
import com.EEITG3.Airbnb.users.service.HostService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class HostController {

	private HostService service;
	@Autowired
	public HostController(HostService service) {
		this.service = service;
	}
	
	private static final Logger log = LoggerFactory.getLogger(HostController.class);
	
//前台功能
	//房東登入
	@PostMapping("/hosts/login")
	public ResponseEntity<?> logIn(@RequestBody LogInRequest request, HttpServletResponse response){
		try {
			String token = service.hostLogin(request);
			CookieUtil.saveHostCookie(response, token);
			return ResponseEntity.ok(token);
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}
	
	//房東註冊
	@PostMapping("/hosts/signup")
	public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request,BindingResult bindingResult, HttpServletResponse response){
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
		service.hostSignUp(request);
		return ResponseEntity.ok("已送出驗證信");
	}
	
	//接收驗證信
	@GetMapping("/hosts/verify")
	public ResponseEntity<?> verify(@RequestParam("token") String token, HttpServletResponse response){
		String jwt = service.verify(token);
		CookieUtil.saveHostCookie(response, jwt);
		return ResponseEntity.ok("驗證成功");
	}
	
	//房東登出
	@PostMapping("/hosts/logout")
	public ResponseEntity<?> logout(HttpServletResponse response){
		CookieUtil.deleteHostCookie(response);
		return ResponseEntity.ok("登出成功");
	}
	
	//房東更新資料
	@PatchMapping("/hosts/update")
	public ResponseEntity<?> update(@RequestBody Map<String, Object> patchPayload, @AuthenticationPrincipal HostDetails hostDetails){
		try {
			Host host = service.hostUpdate(patchPayload, hostDetails);
			return ResponseEntity.ok(host);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
	
	//更新大頭照
	@PostMapping(value = "/hosts/avatar",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateAvatar(@RequestPart MultipartFile avatar,@AuthenticationPrincipal HostDetails hostDetails){
		try {
			Host host = service.updateAvatar(service.currentHost(hostDetails), avatar);
			return ResponseEntity.ok(host);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	//找個人資料
	@GetMapping("/hosts/current")
	public ResponseEntity<?> getAllHosts(@AuthenticationPrincipal HostDetails hostDetails){
		Host result = service.currentHost(hostDetails);
		return ResponseEntity.ok(result);
	}
	
//後台功能
	//找全部房東
	@GetMapping("/admins/hosts")
	public List<Host> getAllHost(){
		return service.findAllHosts();
	}
	
	//更改權限
	@PatchMapping("/admins/hosts/updatePermission")
	public ResponseEntity<?> updatePermission(@RequestBody Map<String, Object> data){
		try {
			String status = (String) data.get("status");
			String email = (String) data.get("email");
			service.permission(status, email);
			String adminId = MDC.get("userId");
			log.info("更改房東權限，房東：%s;操作者：%s".formatted(email,adminId));
			return ResponseEntity.ok(Map.of("message","success"));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	//模糊查詢
	@GetMapping("/admins/hosts/findlike")
	public ResponseEntity<?> findHostLike(@RequestParam String keyword, @RequestParam String context){
		List<Host> hosts = new ArrayList<Host>();
		switch(keyword) {
			case("email"):{
				hosts = service.findLikeByEmail(context);
				break;
			}
			case("username"):{
				hosts = service.findLikeByUsername(context);
				break;
			}
			case("phone"):{
				hosts = service.findLikeByPhone(context);
				break;
			}
			default:{
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("\"message\":\"keyword輸入錯誤\"");
			}
		}
		if(hosts.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("\"message\":\"找不到客戶\"");
		}
		String adminId = MDC.get("userId");
		log.info("查詢房東資料，操作者：%s".formatted(adminId));
		return ResponseEntity.ok(hosts);
	}
	
}
