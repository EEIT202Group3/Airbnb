package com.EEITG3.Airbnb.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.users.service.AdminService;

@RestController
@RequestMapping("/api/admins")
public class AdminController {
	
	private AdminService adminService;
	
	@Autowired
	public AdminController(AdminService adminService) {
		super();
		this.adminService = adminService;
	}

	@GetMapping()
	public ResponseEntity<?> getAllAdmins(){
		return ResponseEntity.ok(adminService.getAllAdmins());
	}
}
