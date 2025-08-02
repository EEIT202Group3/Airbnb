package com.EEITG3.Airbnb.users.service;

import java.util.List;
import java.util.Map;

import com.EEITG3.Airbnb.users.entity.Admin;

public interface AdminService {

	//登入、登出Spring Security會做好
	//找全部員工
	List<Admin> getAllAdmins();
	//新增帳號
	Admin newAdmin(Admin admin);
	//管理權限
	Admin alterAdmin(String adminId, Map<String, Object> patchPayload);
}
