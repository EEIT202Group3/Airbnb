package com.EEITG3.Airbnb.users.service;

import com.EEITG3.Airbnb.users.entity.Customer;

public interface CustomerService {

	//前台功能
	//客戶登入
	Customer customerLogin(String email);
	//客戶註冊
	Customer customerSignup(Customer customer);
	//客戶更新資料
	Customer customerUpdate(Customer customer);
	
	//後台功能
	//停權、啟用
	Customer permission(String status,String customerId);
}
