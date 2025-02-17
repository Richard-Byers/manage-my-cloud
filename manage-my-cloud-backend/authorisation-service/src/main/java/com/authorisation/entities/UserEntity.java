package com.authorisation.entities;

import jakarta.persistence.*;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private boolean firstLogin = true;
    @NaturalId(mutable = true)
    @Column(unique = true)
    private String email;
    private String password;
    private String role;
    private boolean isEnabled = false;
    private String accountType;
    @Embedded
    private LinkedAccounts linkedAccounts;
    @Column(columnDefinition = "bytea")
    private byte[] profileImage;
}
