package ru.vasyunin.springcloudrive.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "photourl")
    private String photoUrl;

    @Column(name = "isactive")
    private boolean isActive;

    @Column(name = "isexpiried")
    private boolean isExpiried;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "lastseen")
    private LocalDateTime lastseen;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))

    private List<Role> roles;

    /**
     * Check if user has the role
     * @param role String Name of role
     * @return Return true if the user has the role
     */
    public boolean hasRoles(String role){
        if (roles == null || role.equals("")) return false;
        for (Role value : roles) {
            if (value.getRole_name().equals(role)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "User: id=" + id + ", username= " + username + ", " + firstName + ' ' + lastName;
    }
}
