package ru.vasyunin.springcloudrive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import ru.vasyunin.springcloudrive.entity.User;

import javax.servlet.http.HttpSession;

@Component
public class SessionUser {
    @Bean(name = "SessionUser")
    @Scope( value = WebApplicationContext.SCOPE_SESSION,
            proxyMode = ScopedProxyMode.TARGET_CLASS)
    public User getMyBean(HttpSession session){
        return  (User)session.getAttribute("user");
    }
}
