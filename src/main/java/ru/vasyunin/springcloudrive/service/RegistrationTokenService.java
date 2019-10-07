package ru.vasyunin.springcloudrive.service;

import org.springframework.stereotype.Service;
import ru.vasyunin.springcloudrive.entity.RegistrationToken;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.repository.RegistrationTokenRepository;
import ru.vasyunin.springcloudrive.validator.FieldMatch;

import java.util.List;

@Service
public class RegistrationTokenService {
    private final RegistrationTokenRepository tokenRepository;

    public RegistrationTokenService(RegistrationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public RegistrationToken createRegistrationToken(User user) {
        RegistrationToken token = tokenRepository.findRegistrationTokenByUser(user);
        if (token != null) tokenRepository.delete(token);
        token = new RegistrationToken();
        token.setUser(user);
        return tokenRepository.saveAndFlush(token);
    }
}
