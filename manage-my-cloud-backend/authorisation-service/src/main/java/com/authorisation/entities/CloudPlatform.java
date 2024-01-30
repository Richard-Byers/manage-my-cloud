package com.authorisation.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CloudPlatform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    private UserEntity userEntity;
    @Column(name = "platform_name")
    private String platformName;
    @Column(length = 2000)
    private String accessToken;
    @Column(length = 2000)
    private String refreshToken;

}
