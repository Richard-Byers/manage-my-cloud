package com.authorisation.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    private String firstName;
    @Getter
    private String lastName;
    @NaturalId(mutable = true)
    private String email;
    @Getter
    private String password;
    @Getter
    private String role;
    @Getter
    private boolean isEnabled = false;
    @Getter
    @Setter
    private String refreshToken;
    @Getter
    @Setter
    private String accountType;
    @Getter
    @Setter
    private String googleProfileImageUrl;

}
