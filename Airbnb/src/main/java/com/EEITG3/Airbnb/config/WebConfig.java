package com.EEITG3.Airbnb.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 將 /images/** 映射到本機磁碟位置
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:D:/pohto/")
                .setCachePeriod(3600); 
    }
}
