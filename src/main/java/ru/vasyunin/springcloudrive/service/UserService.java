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

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, RoleService roleService, DirectoryService directoryService, RegistrationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.directoryService = directoryService;
        this.tokenRepository = tokenRepository;
    }

    public User getUserById(long id){
        return userRepository.getOne(id);
    }

    public User getUserByUsername(String username){
        return userRepository.findUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
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

    /**
     * Creates new user from userDTO. Also creates directory in storage
     * @param userDto DTO object
     * @return User
     */
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
     * Creates new user and directory based on User-object
     * @param user
     * @return User
     */
    public User createUser(User user){
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
        User user = userRepository.findUserByUsername(email);
        if (user == null || user.isActive() || user.getToken() == null || !user.getToken().getToken().equals(token))
            return false;

        user.setActive(true);
        tokenRepository.delete(user.getToken());
        return true;
    }
}
