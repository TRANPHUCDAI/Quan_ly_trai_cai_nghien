package com.trangvi.Quan_ly_trai_cai_nghien.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Cho phép toàn bộ các đường dẫn API
                        .allowedOriginPatterns("*") // Cho phép cổng Front-end React
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Các phương thức được phép
                        .allowedHeaders("*") // Cho phép tất cả các Header
                        .allowCredentials(true); // Cho phép gửi kèm cookie/bảo mật nếu cần
            }
        };
    }
}