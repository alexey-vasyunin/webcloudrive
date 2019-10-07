package ru.vasyunin.springcloudrive.service;

import org.springframework.stereotype.Service;
import ru.vasyunin.springcloudrive.entity.RegistrationToken;
import ru.vasyunin.springcloudrive.entity.User;

@Service
public class MailService {
    private final RegistrationTokenService tokenService;

    public MailService(RegistrationTokenService tokenService) {
        this.tokenService = tokenService;
    }

    public boolean sendRegistrationMessage(User user){
        RegistrationToken token = tokenService.createRegistrationToken(user);
        //todo: send mail
        return true;
    }
}
