package com.EEITG3.Airbnb.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Value("${app.storage.base-dir}")
    private String baseDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
    	String avatarPath = Paths.get(baseDir, "avatar", "customers").toAbsolutePath().toUri().toString();
    	
    	// 將 /images/** 映射到本機磁碟位置
        registry.addResourceHandler("/images/listing/**")
                .addResourceLocations("file:D:/photo/listing/")
                .setCachePeriod(3600); 
        registry.addResourceHandler("/images/avatar/customers/**")
        		.addResourceLocations(avatarPath)
        		.setCachePeriod(3600);
    }
}
