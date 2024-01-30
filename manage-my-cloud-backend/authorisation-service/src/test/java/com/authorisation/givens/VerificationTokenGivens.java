package com.authorisation.givens;

import com.authorisation.entities.LinkedAccounts;
import com.authorisation.entities.UserEntity;
import com.authorisation.entities.VerificationToken;

import java.util.Date;

public class VerificationTokenGivens {

    public static VerificationToken generateEnabledEntityToken() {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken("token");
        verificationToken.setExpiryTime(new Date());
        verificationToken.setUserEntity(new UserEntity(
                1L,
                "John",
                "Doe",
                "johndoe@gmail.com",
                "password",
                "USER",
                true,
                null,
                null,
                null,
                new LinkedAccounts()
        ));
        return verificationToken;
    }

    public static VerificationToken generateDisabledEntityToken() {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken("token");
        verificationToken.setExpiryTime(new Date());
        verificationToken.setUserEntity(new UserEntity(
                1L,
                "John",
                "Doe",
                "johndoe@gmail.com",
                "password",
                "USER",
                false,
                null,
                null,
                null,
                new LinkedAccounts()
        ));
        return verificationToken;
    }

}
