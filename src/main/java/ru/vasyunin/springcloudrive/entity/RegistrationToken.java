package ru.vasyunin.springcloudrive.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "registration_token")
@NoArgsConstructor
public class RegistrationToken implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    @Column(name = "token", insertable = false, unique = true)
    private String token;

    @Column(name = "created", insertable = false)
    private LocalDateTime created;

    public RegistrationToken(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "RegistrationToken: user=" + user.getUsername() + ", token=" + token + ", created=" + created + "; ";
    }
}
