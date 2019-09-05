package ru.vasyunin.springcloudrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class MainController {
    @GetMapping("/")
    public String index(Model model, Principal principal){
        model.addAttribute("user", principal);
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
}
