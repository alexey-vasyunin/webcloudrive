package ru.vasyunin.springcloudrive.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@PropertySource("classpath:private.properties")
@PropertySource("classpath:private.yml")
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/forgot").setViewName("forgot-password");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/tables").setViewName("tables");
    }

}