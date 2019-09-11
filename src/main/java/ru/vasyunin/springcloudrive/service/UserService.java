package ru.vasyunin.springcloudrive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.vasyunin.springcloudrive.UserPrincipal;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
    private UserRepository userRepository;

    public User getUserById(long id){
        return userRepository.getOne(id);
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        System.out.println(user);
        if (user == null) throw new UsernameNotFoundException("User not found in database");
        return new UserPrincipal(user);
    }
}
