package com.authorisation.registration.token;

import com.authorisation.user.UserEntity;
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
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String token;
    private Date expiryTime;

    private static final int EXPIRATION_TIME_MINUTES = 10;

    @OneToOne
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

    public VerificationToken(String token, UserEntity userEntity) {
        super();
        this.token = token;
        this.userEntity = userEntity;
        this.expiryTime = calculateExpiryTime();
    }

    public VerificationToken(String token) {
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
