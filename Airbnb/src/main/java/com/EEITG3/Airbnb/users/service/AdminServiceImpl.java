package com.EEITG3.Airbnb.users.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.EEITG3.Airbnb.users.repository.UserAdminRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.EEITG3.Airbnb.users.entity.Admin;

@Service
public class AdminServiceImpl implements AdminService {

	private UserAdminRepository repo;
	private ObjectMapper objectMapper;
	
	@Autowired
	public AdminServiceImpl(UserAdminRepository repo) {
		super();
		this.repo = repo;
	}
	
	@Override
	public List<Admin> getAllAdmins() {
		return repo.findAll();
	}

	@Override
	public Admin newAdmin(Admin admin) {
		return repo.save(admin);
	}

	@Override
	public Admin alterAdmin(String adminId, Map<String, Object> patchPayload) {
		Optional<Admin> temp = repo.findById(adminId);
		if(!temp.isPresent()) {
			throw new RuntimeException("找不到帳號");
		}
		Admin admin = temp.get();
		Admin updatedAdmin = apply(admin, patchPayload);
		return repo.save(updatedAdmin);
	}
	private Admin apply(Admin admin, Map<String, Object> patchPayload) {
		ObjectNode patchNode = objectMapper.convertValue(patchPayload, ObjectNode.class);
		ObjectNode adminNode = objectMapper.convertValue(admin, ObjectNode.class);
		adminNode.setAll(patchNode);
		return objectMapper.convertValue(adminNode, Admin.class);
	}

	
	
}
