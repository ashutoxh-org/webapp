package com.csye6225.cloud.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * The type User.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Setter
    @Column(nullable = false)
    private String firstName;

    @Setter
    @Column(nullable = false)
    private String lastName;

    @Setter
    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Setter
    @Column(nullable = false)
    private boolean isVerified;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime accountCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime accountUpdated;

    private String tokenToVerify;

    private LocalDateTime tokenExpiryTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
