package com.EEITG3.Airbnb.users.service;


import java.util.Map;

import com.EEITG3.Airbnb.users.dto.LogInRequest;
import com.EEITG3.Airbnb.users.dto.SignUpRequest;

import com.EEITG3.Airbnb.users.entity.Customer;

public interface CustomerService {

	//前台功能
	//客戶登入
	String customerLogin(LogInRequest request);
	//客戶註冊
	Customer customerSignup(SignUpRequest request);
	//客戶更新資料
	Customer customerUpdate(String customerId, Map<String, Object> patchPayload);
	
	//後台功能
	//停權、啟用
	Customer permission(String status,String customerId);
}
