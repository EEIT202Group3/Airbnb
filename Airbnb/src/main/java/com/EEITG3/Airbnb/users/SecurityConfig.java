package com.EEITG3.Airbnb.users;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		//設定不同 URL 路徑的授權規則
		http.authorizeHttpRequests(configurer ->
			configurer
				.anyRequest().permitAll()
		);
		
		//啟用 HTTP Basic 認證
		http.httpBasic(Customizer.withDefaults());
		
		//關閉 CSRF 防護，REST API 不需要
		http.csrf(csrf -> csrf.disable());
		
		//回傳 SecurityFilterChain 物件，Spring Boot 就會使用這條安全規則
		return http.build();
	}
}
