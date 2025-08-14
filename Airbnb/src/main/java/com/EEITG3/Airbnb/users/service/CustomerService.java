package com.EEITG3.Airbnb.users.service;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.EEITG3.Airbnb.users.dto.LogInRequest;
import com.EEITG3.Airbnb.users.dto.SignUpRequest;

import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.entity.CustomerDetails;

public interface CustomerService {

//前台功能
	//客戶登入
	String customerLogin(LogInRequest request);
	//客戶註冊
	void customerSignup(SignUpRequest request);
	//接收驗證信
	String verify(String token);
	//查看個人資料
	Customer currentCustomer(CustomerDetails customerDetails);
	//客戶更新資料
	Customer customerUpdate(Map<String, Object> patchPayload, CustomerDetails customerDetails);
	//登出
	
	//更新大頭貼
	Customer updateAvatar(Customer customer, MultipartFile avatar) throws IOException;
	//忘記密碼時送驗證信
	void forgetPwd(String email);
	
	
//後台功能
	//找全部客戶
	List<Customer> findAllCustomers();
	//停權、啟用
	Customer permission(String status,String customerEmail);
	//模糊查詢email
	List<Customer> findLikeByEmail(String email);
	//模糊查詢username
	List<Customer> findLikeByUsername(String username);
	//模糊查詢phone
	List<Customer> findLikeByPhone(String phone);
}
