package ru.vasyunin.springcloudrive.service;

import org.springframework.stereotype.Service;
import ru.vasyunin.springcloudrive.entity.RegistrationToken;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.repository.RegistrationTokenRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Service
@Transactional
public class RegistrationTokenService {
    private final RegistrationTokenRepository tokenRepository;
    private final EntityManager entityManager;


    public RegistrationTokenService(RegistrationTokenRepository tokenRepository, EntityManager entityManager) {
        this.tokenRepository = tokenRepository;
        this.entityManager = entityManager;
    }

    public RegistrationToken createRegistrationToken(User user) {
        if (user.getToken() != null) tokenRepository.delete(user.getToken());
        RegistrationToken token = tokenRepository.save(new RegistrationToken(user));
        entityManager.refresh(token);
        return token;
    }
}
