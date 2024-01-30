package com.authorisation.givens;

import com.authorisation.entities.PasswordResetToken;
import com.authorisation.entities.UserEntity;
import com.authorisation.registration.password.PasswordResetRequest;

import java.util.Calendar;
import java.util.Date;

import static com.authorisation.givens.UserEntityGivens.generateUserEntity;

public class PasswordResetRequestGivens {

    public static PasswordResetRequest generatePasswordResetRequest() {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setEmail("johndoe@gmail.com");
        passwordResetRequest.setNewPassword("password");
        passwordResetRequest.setConfirmPassword("password");
        return passwordResetRequest;
    }

    public static PasswordResetToken generatePasswordResetToken() {
        UserEntity userEntity = generateUserEntity();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken("token");
        passwordResetToken.setUserEntity(userEntity);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, 10);
        passwordResetToken.setExpiryTime(new Date(calendar.getTime().getTime()));
        return passwordResetToken;
    }


}
