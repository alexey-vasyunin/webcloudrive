package ru.vasyunin.springcloudrive;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.vasyunin.springcloudrive.entity.User;

import java.time.LocalDateTime;
import java.util.Collection;

public class UserPrincipal implements UserDetails {
    private User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !user.isExpiried();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

    public String getFirstname(){ return user.getFirstName(); }

    public String getLastname(){ return user.getLastName(); }

    public LocalDateTime getLastseen(){ return user.getLastseen(); }

    public long getId(){
        return user.getId();
    }
}
