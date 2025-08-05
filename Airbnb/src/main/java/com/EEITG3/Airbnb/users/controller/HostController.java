package com.EEITG3.Airbnb.users.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	
	//房東登入
	@PostMapping("/hosts/login")
	public ResponseEntity<?> logIn(@RequestBody LogInRequest request, HttpServletResponse response){
		try {
			String token = service.hostLogin(request);
			CookieUtil.saveHostCookie(response, token);
			return ResponseEntity.ok(token);
		} catch (Exception e) {
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
		String token = service.hostSignUp(request);
		CookieUtil.saveHostCookie(response, token);
		return ResponseEntity.status(HttpStatus.CREATED).body(token);
	}
	
	//找個人資料
	@GetMapping("/hosts/current")
	public ResponseEntity<?> getAllHosts(@AuthenticationPrincipal HostDetails hostDetails){
		Host result = service.currentHost(hostDetails);
		return ResponseEntity.ok(result);
	}
	@GetMapping("/admins/hosts")
	public List<Host> getAllHost(){
		return service.findAllHosts();
	}
	
}
