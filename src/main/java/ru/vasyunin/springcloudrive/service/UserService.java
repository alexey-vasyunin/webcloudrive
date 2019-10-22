package ru.vasyunin.springcloudrive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vasyunin.springcloudrive.dto.UserDto;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.repository.RegistrationTokenRepository;
import ru.vasyunin.springcloudrive.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final DirectoryService directoryService;
    private final RegistrationTokenRepository tokenRepository;
    private final EntityManager entityManager;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, RoleService roleService, DirectoryService directoryService, RegistrationTokenRepository tokenRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.directoryService = directoryService;
        this.tokenRepository = tokenRepository;
        this.entityManager = entityManager;
    }

    public User getUserById(long id){
        return userRepository.getOne(id);
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        System.out.println(user);
        if (user == null) throw new UsernameNotFoundException("User not found in database");
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(),
                !user.isExpiried(),
                true,
                !user.isBlocked(),
                user.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                        .collect(Collectors.toList()));
    }

    public User createUser(UserDto userDto){
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFistName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setCreated(LocalDateTime.now());
        user.setRoles(roleService.getRolesByName("USER"));
        user = userRepository.save(user);
        directoryService.createRootDirectory(user);
        return user;
    }

    /**
     * Confirm email
     * @param email
     * @param token
     * @return
     */
    public boolean confirmUser(String email, String token){
        User user = userRepository.findByUsername(email);
        if (user == null || user.isActive() || user.getToken() == null || !user.getToken().getToken().equals(token))
            return false;

        user.setActive(true);
        tokenRepository.delete(user.getToken());
        return true;
    }

}
