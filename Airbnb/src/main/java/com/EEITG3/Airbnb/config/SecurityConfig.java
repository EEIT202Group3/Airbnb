package com.EEITG3.Airbnb.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.EEITG3.Airbnb.jwt.JwtFilter;
import com.EEITG3.Airbnb.users.service.CustomerDetailsService;

@Configuration
public class SecurityConfig {

	private JwtFilter jwtFilter;
	private CustomerDetailsService customerDetailsService;
	
	@Autowired
	public SecurityConfig(JwtFilter jwtService, CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
		this.jwtFilter = jwtService;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}
	
	@Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customerDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
	
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();

    }
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		//設定不同 URL 路徑的授權規則
		http.authorizeHttpRequests(configurer ->
			configurer
				//只開放登入、註冊不需要驗證
				.requestMatchers("/api/customers/login", "/api/customers/signup","/api/hosts/login","/api/hosts/signup").permitAll()
				//其他都需要驗證
				.anyRequest().authenticated()
		);
		
		//不使用 session
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		//把 JWT 驗證加到其他 Filter 前面
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		
		//啟用 HTTP Basic 認證
		http.httpBasic(Customizer.withDefaults());
		
		//關閉 CSRF 防護，REST API 不需要
		http.csrf(csrf -> csrf.disable());
		
		//開啟並設定 CORS 支援、套用下面的 CORS 設定
		http.cors(Customizer.withDefaults());
		
		//回傳 SecurityFilterChain 物件，Spring Boot 就會使用這條安全規則
		return http.build();
	}
	
	//CORS 設定
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return source;
	}
}
