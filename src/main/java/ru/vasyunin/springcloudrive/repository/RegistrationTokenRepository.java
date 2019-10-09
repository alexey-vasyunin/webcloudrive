package ru.vasyunin.springcloudrive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vasyunin.springcloudrive.entity.RegistrationToken;

@Repository
public interface RegistrationTokenRepository extends JpaRepository<RegistrationToken, Long> {
}
