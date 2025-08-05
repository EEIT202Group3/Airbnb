package com.EEITG3.Airbnb.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.EEITG3.Airbnb.jwt.JwtFilter;
import com.EEITG3.Airbnb.users.service.CustomerDetailsService;
import com.EEITG3.Airbnb.users.service.HostDetailsService;

import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

	private JwtFilter jwtFilter;
	private CustomerDetailsService customerDetailsService;
	private HostDetailsService hostDetailsService;
	
	@Autowired
	public SecurityConfig(JwtFilter jwtService, CustomerDetailsService customerDetailsService, HostDetailsService hostDetailsService) {
		this.customerDetailsService = customerDetailsService;
		this.jwtFilter = jwtService;
		this.hostDetailsService = hostDetailsService;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}
	
	//Customer的provider，負責驗證customer的帳號密碼
	@Bean
    public AuthenticationProvider customerAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customerDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
	//Host的provider，負責驗證host的帳號密碼
	@Bean
	public AuthenticationProvider hostAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(hostDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
	
	//統一調用所有provider，要把customer、host的provider都放進來
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		List<AuthenticationProvider> providers = List.of(
		        customerAuthenticationProvider(),
		        hostAuthenticationProvider()
		    );
		return new ProviderManager(providers);
    }
	
	//設定只有客戶能用的API
	@Bean
	@Order(3)
	public SecurityFilterChain customerFilterChain(HttpSecurity http) throws Exception {
		return http.securityMatcher("/api/customers/**")
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.csrf(csrf->csrf.disable())
				.cors(Customizer.withDefaults())
				.authorizeHttpRequests(configurer->
					configurer
					.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
					.requestMatchers("/api/customers/login","/api/customers/signup").permitAll()
					.anyRequest().hasRole("CUSTOMER"))
				.build();	
	}
	
	//設定只有房東能用的API
	@Bean
	@Order(2)
	public SecurityFilterChain hostFilterChain(HttpSecurity http) throws Exception {
		return http.securityMatcher("/api/hosts/**")
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.csrf(csrf->csrf.disable())
				.cors(Customizer.withDefaults())
				.authorizeHttpRequests(configurer->
					configurer
					.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
					.requestMatchers("/api/hosts/login","/api/hosts/signup").permitAll()
					.anyRequest().hasRole("HOST"))
				.build();	
	}
	
	//設定後台的權限
	@Bean
	@Order(1)
	public SecurityFilterChain adminFilterChain(HttpSecurity http, DataSource dataSource) throws Exception {
		//告訴Spring要去哪裡找使用者、權限
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
		jdbcUserDetailsManager.setUsersByUsernameQuery(
				"SELECT admin_id AS username, password, is_active AS enabled FROM admins WHERE admin_id = ?");
		jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
				"SeLECT admin_id AS username, authority FROM authorities WHERE admin_id=?");
		//避免用到其他人的provider，所以在內部設定provider
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(jdbcUserDetailsManager);
        provider.setPasswordEncoder(passwordEncoder());
		//設定權限限制
        return http
                .securityMatcher("/api/admins/**")
                .authenticationProvider(provider)
                .authorizeHttpRequests(auth -> auth
                	.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/admins/login", "/api/admins/logout").permitAll()
                    .anyRequest().hasRole("ADMIN")
                )
                //設定表單登入
                .formLogin(form -> form
                    .loginProcessingUrl("/api/admins/login") // 登入 API
                    .usernameParameter("username") // 對應表單欄位名
                    .passwordParameter("password")
                    .successHandler((request, response, authentication) -> {
                        response.setStatus(HttpServletResponse.SC_OK);
                    })
                    .failureHandler((request, response, exception) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    })
                )
                //設定登出
                .logout(logout -> logout
                    .logoutUrl("/api/admins/logout") // 登出 API
                    .logoutSuccessHandler((request, response, auth) -> {
                        response.setStatus(HttpServletResponse.SC_OK);
                    })
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                )
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admins/login", "/api/admins/logout").permitAll()
                        .anyRequest().hasRole("ADMIN")
                		)
                .build();
	}
	
	@Bean
	@Order(4)
	public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
	    return http
	        .securityMatcher("/api/**")
	        .csrf(csrf -> csrf.disable())
	        .cors(Customizer.withDefaults())
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
	            .anyRequest().permitAll())
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
	
	
	
	
	// 修維的圖片 API
	@Bean
    public WebMvcConfigurer resourceConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/images/**")
                        .addResourceLocations("file:/Users/youm/pohto/")
                        .setCachePeriod(3600);
            }
        };
    }
}
