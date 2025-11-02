package com.huy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.huy.constant.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Value("${upload.avatar.path}")
	private String uploadPath;
	
	@Value("${default.avatar.path}")
	private String defaultAvatarPath;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(Paths.avatar_url + "/**")
		.addResourceLocations("file:" + uploadPath + "/");
		
		registry.addResourceHandler(Paths.default_avatar_url + "/**")
		.addResourceLocations("file:" + defaultAvatarPath + "/");
	}
}
