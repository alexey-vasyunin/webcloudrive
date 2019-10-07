package ru.vasyunin.springcloudrive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vasyunin.springcloudrive.entity.RegistrationToken;
import ru.vasyunin.springcloudrive.entity.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegistrationTokenRepository extends JpaRepository<RegistrationToken, String> {
    RegistrationToken findRegistrationTokenByUser(User user);
}
