package com.EEITG3.Airbnb.users.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.jwt.JwtService;
import com.EEITG3.Airbnb.users.dto.LogInRequest;
import com.EEITG3.Airbnb.users.dto.SignUpRequest;
import com.EEITG3.Airbnb.users.entity.Host;
import com.EEITG3.Airbnb.users.entity.HostDetails;
import com.EEITG3.Airbnb.users.repository.HostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class HostServiceImpl implements HostService {

	private HostRepository repo;
	private ObjectMapper objectMapper;
	private JwtService jwtService;
	private AuthenticationManager authManager;
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	public HostServiceImpl(HostRepository repo, ObjectMapper objectMapper, JwtService jwtService,
			AuthenticationManager authManager, BCryptPasswordEncoder encoder) {
		super();
		this.repo = repo;
		this.objectMapper = objectMapper;
		this.jwtService = jwtService;
		this.authManager = authManager;
		this.encoder = encoder;
	}

	@Override
	public String hostLogin(LogInRequest request) {
		Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		if(authentication.isAuthenticated()) {
			return jwtService.generateToken(request.getEmail(), "ROLE_HOST");
		} else {
			throw new BadCredentialsException("驗證失敗");
		}
	}

	@Override
	public String hostSignUp(SignUpRequest request) {
		Optional<Host> temp = repo.findHostByEmail(request.getEmail());
		if(temp.isPresent()) {
			throw new IllegalArgumentException("已註冊");
		}
		String encodedPassword = encoder.encode(request.getPassword());
		Host host = new Host(request.getEmail(),encodedPassword,request.getUsername(),request.getPhone());
		repo.save(host);
		return jwtService.generateToken(host.getEmail(),"ROLE_HOST");
	}

	@Override
	public Host currentHost(HostDetails hostDetails) {
		Optional<Host> temp = repo.findHostByEmail(hostDetails.getUsername());
		if(!temp.isPresent()) {
			throw new RuntimeException("找不到房東");
		}
		Host host = temp.get();
		return host;
	}

	@Override
	public Host hostUpdate(Map<String, Object> patchPayload, HostDetails hostDetails) {
		Optional<Host> temp = repo.findHostByEmail(hostDetails.getUsername());
		if(!temp.isPresent()) {
			throw new RuntimeException("找不到房東");
		}
		Host host = temp.get();
		Host updatedHost = apply(patchPayload, host);
		return repo.save(updatedHost);
	}
	private Host apply(Map<String, Object> patchPayload, Host host) {
		ObjectNode customerNode = objectMapper.convertValue(host, ObjectNode.class);
		ObjectNode patchNode = objectMapper.convertValue(patchPayload, ObjectNode.class);
		customerNode.setAll(patchNode);
		return objectMapper.convertValue(customerNode, Host.class);
	}
}
