package com.EEITG3.Airbnb.users.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.EEITG3.Airbnb.jwt.EmailService;
import com.EEITG3.Airbnb.jwt.JwtService;
import com.EEITG3.Airbnb.users.dto.LogInRequest;
import com.EEITG3.Airbnb.users.dto.MonthlyRegist;
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
	private PasswordEncoder encoder;
	private EmailService emailService;
	
	@Value("${app.storage.base-dir}")
	private String baseDir;
	
	@Autowired
	public HostServiceImpl(HostRepository repo, ObjectMapper objectMapper, JwtService jwtService,
			AuthenticationManager authManager, PasswordEncoder encoder, EmailService emailService) {
		super();
		this.repo = repo;
		this.objectMapper = objectMapper;
		this.jwtService = jwtService;
		this.authManager = authManager;
		this.encoder = encoder;
		this.emailService = emailService;
	}

	@Override
	public String hostLogin(LogInRequest request) {
		Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		if(!authentication.isAuthenticated()) {
			throw new BadCredentialsException("驗證失敗");
		}
		Optional<Host> temp = repo.findHostByEmail(request.getEmail());
		if(!temp.isPresent()) {
			throw new BadCredentialsException("帳號不存在");
		}
		Host host = temp.get();
		return jwtService.generateToken(host.getEmail(), "ROLE_HOST");
	}

	@Override
	public void hostSignUp(SignUpRequest request) {
		Optional<Host> temp = repo.findHostByEmail(request.getEmail());
		if(temp.isPresent()) {
			throw new IllegalArgumentException("已註冊");
		}
		String encodedPassword = encoder.encode(request.getPassword());
		Host host = new Host(request.getEmail(),encodedPassword,request.getUsername(),request.getPhone());
		String token = UUID.randomUUID().toString();
		host.setVerificationToken(token);
		host.setVerified(false);
		repo.save(host);
		emailService.sendHostVerificationEmail(host.getEmail(), token, host.getUsername());
	}
	
	@Override
	public String verify(String token) {
		Optional<Host> temp = repo.findHostByToken(token);
		if(!temp.isPresent()) {
			throw new RuntimeException("無效的連結");
		}
		Host host = temp.get();
		host.setVerificationToken(null);
		host.setVerified(true);
		repo.save(host);
		return jwtService.generateToken(host.getEmail(), "ROLE_HOST");
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
	
	@Override
	public Host updateAvatar(Host host, MultipartFile avatar) throws IOException {
		Path avatarDir = Paths.get(baseDir,"avatar","hosts");
		Files.createDirectories(avatarDir);
		String ext = getExtension(avatar.getOriginalFilename());
		String filename = host.getHostId()+"."+ext;
		Files.copy(avatar.getInputStream(), avatarDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
		String avatarURL = "/images/avatar/hosts/"+filename;
		host.setAvatarURL(avatarURL);
		return repo.save(host);
	}
	
	private String getExtension(String filename) {
		if(filename==null) return "png";
		int dotIndex = filename.lastIndexOf('.');
		return(dotIndex>=0)?filename.substring(+1).toLowerCase():"png";
	}

	@Override
	public List<Host> findAllHosts() {
		return repo.findAll();
	}

	@Override
	public Host permission(String status, String hostEmail) {
		Optional<Host> temp = repo.findHostByEmail(hostEmail);
		Host host = new Host();
		if(temp.isPresent()) {
			host = temp.get();
		}
		switch (status){
		case "ACTIVE": {
			host.setActive(true);
			break;
		}
		case "SUSPEND": {
			host.setActive(false);
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + status);
		}
		//把更新後的狀態存回資料庫、回傳更新後的資料
		return repo.save(host);
	}

	@Override
	public List<Host> findLikeByEmail(String email) {
		String likeEmail = "%"+email+"%";
		return repo.findLikeByEmail(likeEmail);
	}

	@Override
	public List<Host> findLikeByUsername(String username) {
		String likeUsername = "%"+username+"%";
		return repo.findLikeByUsername(likeUsername);
	}

	@Override
	public List<Host> findLikeByPhone(String phone) {
		String likePhone = "%"+phone+"%";
		return repo.findLikeByPhone(likePhone);
	}

	@Override
	public Map<String, Object> getMonthlyRegist() {
		List<MonthlyRegist> monthlyRegists = repo.getMonthlyRegist();
		Map<String, Object> result = new HashMap<String, Object>();
		List<String> months = new ArrayList<String>();
		List<Integer> registrations = new ArrayList<Integer>();
		for(MonthlyRegist data : monthlyRegists) {
			months.add(data.getMonth());
			registrations.add(data.getRegistrations());
		}
		result.put("months", months);
		result.put("registrations", registrations);
		return result;
	}

	@Override
	public Integer getTotalHosts() {
		return repo.getTotalHosts();
	}

	@Override
	public Integer getVerifiedHosts() {
		return repo.getVerifiedHosts();
	}

	

	
}
