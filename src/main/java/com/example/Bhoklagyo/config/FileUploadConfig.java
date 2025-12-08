package com.example.Bhoklagyo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileUploadConfig {
    
    @Value("${file.upload.dir:uploads/restaurant-images}")
    private String uploadDir;
    
    public String getUploadDir() {
        return uploadDir;
    }
}
