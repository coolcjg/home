package com.cjg.home.config.storage;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void  addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/storage/**") // --1
                .addResourceLocations("file:///D://NAS//home_storage/"); //--2
    }

}