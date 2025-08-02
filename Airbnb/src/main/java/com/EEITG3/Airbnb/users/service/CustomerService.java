package com.EEITG3.Airbnb.users.service;


import java.util.List;
import java.util.Map;

import com.EEITG3.Airbnb.users.dto.LogInRequest;
import com.EEITG3.Airbnb.users.dto.SignUpRequest;

import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.entity.CustomerDetails;

public interface CustomerService {

	//前台功能
	//客戶登入
	String customerLogin(LogInRequest request);
	//客戶註冊
	String customerSignup(SignUpRequest request);
	//查看個人資料
	Customer currentCustomer(CustomerDetails customerDetails);
	//客戶更新資料
	Customer customerUpdate(Map<String, Object> patchPayload, CustomerDetails customerDetails);
	//登出
	
	//後台功能
	//找全部客戶
	List<Customer> findAllCustomers();
	//停權、啟用
	Customer permission(String status,String customerId);
}
