package ru.vasyunin.springcloudrive.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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
}
