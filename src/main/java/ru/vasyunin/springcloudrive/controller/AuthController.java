package ru.vasyunin.springcloudrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vasyunin.springcloudrive.dto.UserDto;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.service.MailService;
import ru.vasyunin.springcloudrive.service.UserService;

import javax.validation.Valid;

@Controller
public class AuthController {

    private final UserService userService;
    private final MailService mailService;

    @Autowired
    public AuthController(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        UserDto userDTO = new UserDto();
        model.addAttribute("userDto", userDTO);
        return "register";
    }

    @PostMapping("/register/form")
    public String registerUser(@Valid @ModelAttribute("userDto") UserDto userDto,
                               BindingResult bindingResult,
                               Model model){

        if (userService.getUserByUsername(userDto.getUsername()) != null){
            bindingResult.addError(new FieldError("userDto", "username", userDto.getUsername(), false, null, null, "Username has already registered"));
        };

        // If dto has some errors show register form
        if (bindingResult.hasErrors()) {
            return "register";
        }

        User user = userService.createUser(userDto);

        mailService.sendRegistrationMessage(user);

        return "redirect:/";
    }
}
