package ru.vasyunin.springcloudrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.vasyunin.springcloudrive.dto.UserDto;
import ru.vasyunin.springcloudrive.service.UserService;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        UserDto userDTO = new UserDto();
        model.addAttribute("userDto", userDTO);
        return "register";
    }

    @PostMapping("/register/form")
    public String registerUser(@Valid @ModelAttribute("UserDto") UserDto userDTO,
                               BindingResult bindingResult,
                               Model model){
        model.addAttribute("userDto", userDTO);

        if (bindingResult.hasErrors()) {
            return "register";
        }

//        if (userService.getUserByUsername(userDTO.getUsername()) != null){
//            bindingResult.rejectValue("username", "isexists", "Username has already registered");
//            return "register";
//        };

        System.out.println(userDTO);
        return "register";
    }
}
