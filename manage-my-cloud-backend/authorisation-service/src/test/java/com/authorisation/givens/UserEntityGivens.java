package com.authorisation.givens;

import com.authorisation.entities.LinkedAccounts;
import com.authorisation.entities.UserEntity;

public class UserEntityGivens {

    public static UserEntity generateUserEntity() {
        return new UserEntity(1L, "John", "Doe", false,"johndoe@gmail.com", "password", "USER", false, null, null, new LinkedAccounts(), null);
    }

    public static UserEntity generateUserEntityEnabled() {
        return new UserEntity(1L, "John", "Doe", false,"johndoe@gmail.com", "password", "USER", true, null, null, new LinkedAccounts(), null);
    }

    public static UserEntity generateGoogleUserEntity() {
        return new UserEntity(1L, "John", "Doe", false,"johndoe@gmail.com", "password", "USER", true, "GOOGLE", "picture", new LinkedAccounts(), null);
    }

}
