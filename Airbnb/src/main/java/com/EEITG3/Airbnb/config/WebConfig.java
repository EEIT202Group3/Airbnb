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
        
    	String listingPath = Paths.get(baseDir,"listing").toAbsolutePath().toUri().toString();
    	
    	String customerAvatarPath = Paths.get(baseDir, "avatar", "customers").toAbsolutePath().toUri().toString();
    	
    	String hostAvatarPath = Paths.get(baseDir, "avatar", "hosts").toAbsolutePath().toUri().toString();
    	
    	// 將 /images/** 映射到本機磁碟位置(Mac路徑)
        registry.addResourceHandler("/images/listings/**") 
                .addResourceLocations(listingPath)
        		.setCachePeriod(3600); 
        
        registry.addResourceHandler("/images/avatar/customers/**")
        		.addResourceLocations(customerAvatarPath)
        		.setCachePeriod(3600);
        
        registry.addResourceHandler("/images/avatar/hosts/**")
				.addResourceLocations(hostAvatarPath)
				.setCachePeriod(3600);
    }
}
