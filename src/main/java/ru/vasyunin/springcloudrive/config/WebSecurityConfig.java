package ru.vasyunin.springcloudrive.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.repository.UserDetailsRepo;
import ru.vasyunin.springcloudrive.repository.UserRepository;
import ru.vasyunin.springcloudrive.service.UserService;
import ru.vasyunin.springcloudrive.utils.OAuthClientResource;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
@EnableOAuth2Client
@Order(1000)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomAuthSuccessHandler customAuthSucceessHandler;
    private final UserService userService;
    private final OAuth2ClientContext oAuth2ClientContext;
    private final PrincipalExtractor principalExtractor;

    @Lazy
    @Autowired
    public WebSecurityConfig(CustomAuthSuccessHandler customAuthSucceessHandler, UserService userService, OAuth2ClientContext oAuth2ClientContext, PrincipalExtractor principalExtractor) {
        this.customAuthSucceessHandler = customAuthSucceessHandler;
        this.userService = userService;
        this.oAuth2ClientContext = oAuth2ClientContext;
        this.principalExtractor = principalExtractor;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                    .antMatchers("/home", "/404", "/login", "/login/**", "/register", "/register/**", "/forgot").permitAll()
                    .antMatchers("/css/**", "/js/**", "/vendor/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .successHandler(customAuthSucceessHandler)
                .and()
                    .logout().permitAll()
                .and()
                    .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userService);
        return daoAuthenticationProvider;
    }


    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    private Filter ssoFilter(){
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();

        filters.add(ssoFilter(facebook(), "/login/facebook"));
        filters.add(ssoFilter(google(), "/login/google"));

        filter.setFilters(filters);
        return filter;
    }

    private Filter ssoFilter(OAuthClientResource resource, String path){
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(resource.getClient(), oAuth2ClientContext);
        filter.setRestTemplate(template);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(resource.getResource().getUserInfoUri(), resource.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        tokenServices.setPrincipalExtractor(principalExtractor);
        filter.setTokenServices(tokenServices);
        return filter;
    }

    @Bean
    @ConfigurationProperties("oauth2.facebook")
    public OAuthClientResource facebook(){
        return new OAuthClientResource();
    }


    @Bean
    @ConfigurationProperties("oauth2.google")
    public OAuthClientResource google(){
        return new OAuthClientResource();
    }


    @Bean
    public FilterRegistrationBean<OAuth2ClientContextFilter> oAuth2ClientFilterRegistration(OAuth2ClientContextFilter filter){
        FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }


    @Bean
    public PrincipalExtractor principalExtractor(UserRepository userRepository, HttpServletRequest request){
        return map -> {
            String username = (String)map.get("email");

            User user = userRepository.findUserByUsernameAndIsActiveTrue(username).orElseGet(() -> {
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setActive((boolean)map.get("email_verified"));
                newUser.setFirstName((String) map.get("given_name"));
                newUser.setLastName((String) map.get("family_name"));
                return userRepository.save(newUser);
            });

            user.setLastseen(LocalDateTime.now());

            return user;
        };
    }

}