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

		String listingPath = Paths.get(baseDir, "listings").toAbsolutePath().toUri().toString();

		String customerAvatarPath = Paths.get(baseDir, "avatar", "customers").toAbsolutePath().toUri().toString();

		String hostAvatarPath = Paths.get(baseDir, "avatar", "hosts").toAbsolutePath().toUri().toString();

		String carPicturePath = Paths.get("..", "front-end", "public", "carPicture").toAbsolutePath().toUri()
				.toString();

		String reviewsPath = Paths.get(baseDir, "reviews").toAbsolutePath().toUri().toString();

		// 將 /images/** 映射到本機磁碟位置(Mac路徑)
		registry.addResourceHandler("/images/listings/**").addResourceLocations(listingPath).setCachePeriod(3600);

		registry.addResourceHandler("/images/avatar/customers/**").addResourceLocations(customerAvatarPath)
				.setCachePeriod(0);

		registry.addResourceHandler("/images/avatar/hosts/**").addResourceLocations(hostAvatarPath)
				.setCachePeriod(0);

		registry.addResourceHandler("/carPicture/**").addResourceLocations(carPicturePath).setCachePeriod(3600);

		registry.addResourceHandler("/images/reviews/**").addResourceLocations(reviewsPath).setCachePeriod(3600);

	}
}
