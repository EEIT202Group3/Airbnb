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
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.EEITG3.Airbnb.jwt.JwtFilter;
import com.EEITG3.Airbnb.users.service.CustomerDetailsService;

import javax.sql.DataSource;

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
	
	//設定只有客戶能用的API
	@Bean
	public SecurityFilterChain customerFilterChain(HttpSecurity http) throws Exception {
		return http.securityMatcher("/api/customers/**")
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.httpBasic(Customizer.withDefaults())
				.csrf(csrf->csrf.disable())
				.cors(Customizer.withDefaults())
				.authorizeHttpRequests(configurer->
					configurer
					.requestMatchers("/api/customers/login","/api/customers/signup").permitAll()
					.anyRequest().hasRole("CUSTOMER"))
				.build();	
	}
	
	//設定只有房東能用的API
	@Bean
	public SecurityFilterChain hostFilterChain(HttpSecurity http) throws Exception {
		return http.securityMatcher("/api/hosts/**")
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.httpBasic(Customizer.withDefaults())
				.csrf(csrf->csrf.disable())
				.cors(Customizer.withDefaults())
				.authorizeHttpRequests(configurer->
					configurer
					.requestMatchers("/api/hosts/login","/api/hosts/signup").permitAll()
					.anyRequest().hasRole("HOST"))
				.build();	
	}
	
	//設定後台的權限
	@Bean
	public SecurityFilterChain admFilterChain(HttpSecurity http) throws Exception {
		return http.securityMatcher("/api/admins/**")
				   .httpBasic(Customizer.withDefaults())
				   .csrf(csrf->csrf.disable())
				   .authorizeHttpRequests(configurer -> 
				       configurer.anyRequest().hasRole("ADMIN")
				   )
				   .build();
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
	
	@Bean
	public UserDetailsManager userDetailsManager(DataSource dataSource) {
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
		jdbcUserDetailsManager.setUsersByUsernameQuery(
				"SELECT admin_id,password,is_active FROM admins WHERE admin_id = ?");
		jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
				"SeLECT admin_id,authority FROM authorities WHERE admin_id=?");
		return jdbcUserDetailsManager;
	}
}
