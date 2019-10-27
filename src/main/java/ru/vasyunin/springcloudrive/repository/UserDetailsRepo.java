package ru.vasyunin.springcloudrive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vasyunin.springcloudrive.entity.User;

public interface UserDetailsRepo extends JpaRepository<User, String> {
}
