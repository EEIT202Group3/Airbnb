package com.EEITG3.Airbnb.users.service;

import java.util.List;
import java.util.Map;

import com.EEITG3.Airbnb.users.dto.LogInRequest;
import com.EEITG3.Airbnb.users.dto.SignUpRequest;
import com.EEITG3.Airbnb.users.entity.Host;
import com.EEITG3.Airbnb.users.entity.HostDetails;

public interface HostService {

	//前台功能
	//房東登入
	String hostLogin(LogInRequest request);
	//房東註冊
	void hostSignUp(SignUpRequest request);
	//接收驗證信
	String verify(String token);
	//查看個人資料
	Host currentHost(HostDetails hostDetails);
	//房東更新資料
	Host hostUpdate(Map<String, Object> patchPayload, HostDetails hostDetails);
	//登出
	
	//後台功能
	//找全部房東
	List<Host> findAllHosts();
	//停權、啟用
	Host permission(String status, String hostEmail);
	//模糊查詢email
	List<Host> findLikeByEmail(String email);
	//模糊查詢username
	List<Host> findLikeByUsername(String username);
	//模糊查詢phone
	List<Host> findLikeByPhone(String phone);
}
