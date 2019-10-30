package ru.vasyunin.springcloudrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.vasyunin.springcloudrive.dto.UserDto;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.service.MailService;
import ru.vasyunin.springcloudrive.service.UserService;

import javax.validation.Valid;
import java.util.Base64;

@Controller
@RequestMapping("/register")
public class AuthController {

    private final UserService userService;
    private final MailService mailService;

    @Autowired
    public AuthController(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @GetMapping()
    public String registerForm(Model model) {
        UserDto userDTO = new UserDto();
        model.addAttribute("userDto", userDTO);
        return "register";
    }

    @PostMapping("/form")
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

        if (!mailService.sendRegistrationMessage(user)){
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        };

        return "confirm-email";
    }

    @GetMapping("/confirm/{email64}/{token}")
    public String confirmEmail(@PathVariable("email64") String email64, @PathVariable("token") String token){
        String email = new String(Base64.getUrlDecoder().decode(email64));
        if (userService.confirmUser(email, token))
            return "redirect:/";
        else
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
    }

}
