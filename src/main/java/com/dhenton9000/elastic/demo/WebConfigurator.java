package com.dhenton9000.elastic.demo;

import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfigurator implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        CorsRegistration t = registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST",
                        "PUT", "DELETE", "OPTIONS");
    }
 
}
