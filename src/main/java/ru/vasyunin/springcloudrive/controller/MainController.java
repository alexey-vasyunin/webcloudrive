package ru.vasyunin.springcloudrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.vasyunin.springcloudrive.entity.DirectoryItem;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.service.DirectoryService;
import ru.vasyunin.springcloudrive.service.UserService;

import java.security.Principal;

@Controller
public class MainController {
    private final UserService userService;
    private final DirectoryService directoryService;

    @Autowired
    public MainController(UserService userService, DirectoryService directoryService) {
        this.userService = userService;
        this.directoryService = directoryService;
    }


    @GetMapping("/")
    public String index(Model model, Principal principal){
        User user = userService.getUserByUsername(principal.getName());
        DirectoryItem root = directoryService.getRootDirectory(user);
        model.addAttribute("user", user);
        model.addAttribute("root", root);
        return "index";
    }
}
