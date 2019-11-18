package ru.vasyunin.springcloudrive.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
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

    @Column(name = "isblocked")
    private boolean isBlocked;

    @Column(name = "created")
    @CreationTimestamp
    private LocalDateTime created;

    @Column(name = "lastseen", columnDefinition = "TIMESTAMP")
    private LocalDateTime lastseen;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))

    private List<Role> roles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<DirectoryItem> directories;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<FileEntity> files;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
    private RegistrationToken token;


    public void setLastseenNow(){
        lastseen = LocalDateTime.now();
    }

    public User(String username, String password, String firstName, String lastName, boolean isActive, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.roles = roles;
    }

    /**
     * Check if user has the role
     * @param role String Name of role
     * @return Return true if the user has the role
     */
    public boolean hasRoles(String role){
        if (roles == null || role.equals("")) return false;
        for (Role value : roles) {
            if (value.getRoleName().equals(role)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "User: id=" + id + ", username= " + username + ", " + firstName + ' ' + lastName;
    }
}
