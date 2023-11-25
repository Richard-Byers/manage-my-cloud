package com.authorisation.registration.password;

import com.authorisation.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class PasswordResetToken {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String token;
    private Date expiryTime;

    private static final int EXPIRATION_TIME_MINUTES = 10;

    @OneToOne
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

    public PasswordResetToken(String token, UserEntity userEntity) {
        super();
        this.token = token;
        this.userEntity = userEntity;
        this.expiryTime = calculateExpiryTime();
    }

    public PasswordResetToken(String token) {
        super();
        this.token = token;
        this.expiryTime = calculateExpiryTime();
    }

    public Date calculateExpiryTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME_MINUTES);

        return new Date(calendar.getTime().getTime());
    }

}
