package ru.vasyunin.springcloudrive.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@Configuration
public class FileTransferConfig {
    @Bean
    public MultipartConfigElement multipartConfigElement(){
        MultipartConfigFactory config = new MultipartConfigFactory();
        config.setMaxFileSize(DataSize.ofKilobytes(2048L));
        config.setMaxRequestSize(DataSize.ofKilobytes(2048L));
        return config.createMultipartConfig();
    }
}
