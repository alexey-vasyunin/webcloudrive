package ru.vasyunin.springcloudrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import ru.vasyunin.springcloudrive.UserPrincipal;
import ru.vasyunin.springcloudrive.service.UserService;

import java.security.Principal;

@Controller
public class MainController {
    private UserService userService;


    @GetMapping("/")
    public String index(Model model, Principal principal){
        UserPrincipal user = new UserPrincipal(userService.getUserByUsername(principal.getName()));
        model.addAttribute("user", user);
        return "index";
    }

    @GetMapping("/home")
    public String homePage(){
        return "home";
    }

    @GetMapping("/hello")
    public String helloPage(){
        return "hello";
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

//    @ExceptionHandler
//    public ResponseEntity handlerExceprion(UsernameNotFoundException e){
//
//        return new ResponseEntity()
//    }
}
