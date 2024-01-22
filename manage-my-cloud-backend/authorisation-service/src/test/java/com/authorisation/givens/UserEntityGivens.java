package com.authorisation.givens;

import com.authorisation.entities.LinkedAccounts;
import com.authorisation.entities.UserEntity;

public class UserEntityGivens {

    public static UserEntity generateUserEntity() {
        return new UserEntity(1L, "John", "Doe", "johndoe@gmail.com", "password", "USER", false, new LinkedAccounts());
    }

    public static UserEntity generateUserEntityEnabled() {
        return new UserEntity(1L, "John", "Doe", "johndoe@gmail.com", "password", "USER", true, new LinkedAccounts());
    }

}
