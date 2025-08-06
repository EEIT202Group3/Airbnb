package com.EEITG3.Airbnb.users.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.users.entity.Admin;
import com.EEITG3.Airbnb.users.service.AdminService;

@RestController
@RequestMapping("/api/admins")
public class AdminController {
	
	private AdminService service;
	
	@Autowired
	public AdminController(AdminService service) {
		super();
		this.service = service;
	}

	//登入登出在 Config 檔做好了，這邊不需要做
	
	
	//取得全部員工資料
	@GetMapping()
	public ResponseEntity<?> getAllAdmins(){
		return ResponseEntity.ok(service.getAllAdmins());
	}
	//取得當前登入人員資料
	@GetMapping("/current")
	public ResponseEntity<?> getCurrentAdmin(@AuthenticationPrincipal UserDetails userDetails){
		Admin admin = service.currentAdmin(userDetails);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("adminId", admin.getAdminId());
		result.put("username", admin.getUsername());
		return ResponseEntity.ok(result);
	}
}
