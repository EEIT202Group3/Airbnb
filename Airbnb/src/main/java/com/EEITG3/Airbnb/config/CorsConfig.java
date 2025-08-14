package com.EEITG3.Airbnb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	//藍新外網網域使用，ngrok用
	@Override
public void addCorsMappings(CorsRegistry registry) {
        
        // 🔥 藍新金流專用 CORS - 最寬鬆設定，優先處理
        registry.addMapping("/api/newebPay/**")
                .allowedOriginPatterns("*")  // 允許所有來源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)  // 藍新回調不需要憑證
                .maxAge(3600);
        
        // 一般 API 的 CORS 設定
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:3000", 
                    "http://localhost:8080",
                    "http://localhost:5173",  
                    "http://localhost:5174",
                    "https://eb63c6411934.ngrok-free.app"  //你的 ngrok 網域
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}