package com.authorisation.givens;

import com.authorisation.entities.LinkedAccounts;
import com.authorisation.entities.UserEntity;

import static com.authorisation.TestConstants.TESTER_EMAIL;

public class UserEntityGivens {

    public static UserEntity generateUserEntity() {
        return new UserEntity(1L, "John", "Doe", "johndoe@gmail.com", "password", "USER", false, null, new LinkedAccounts(), null);
    }

    public static UserEntity generateUserEntityWithProfilePicture() {
        byte[] picture = {1, 2, 3, 4, 5};
        return new UserEntity(1L, "John", "Doe", "johndoe@gmail.com", "password", "USER", false, null, null, new LinkedAccounts(), picture);
    }

    public static UserEntity generateUserEntityEnabled() {
        return new UserEntity(1L, "John", "Doe", "johndoe@gmail.com", "password", "USER", true, null, new LinkedAccounts(), null);
    }

    public static UserEntity generateGoogleUserEntity() {
        return new UserEntity(1L, "John", "Doe", "johndoe@gmail.com", "password", "USER", true, "GOOGLE", new LinkedAccounts(), null);
    }

    public static UserEntity generateUserEntityTesterEmail() {
        return new UserEntity(1L, "John", "Doe", TESTER_EMAIL, "password", "USER", true, "GOOGLE", "picture", new LinkedAccounts(), null);
    }

    public static String generateUserData() {
        return "Email: managemycloudtester@gmail.com\n" +
                "First Name: test\n" +
                "Last Name: test\n" +
                "Linked Drives: [Drive Email: managemycloudtester@gmail.com, Drive Type: GoogleDrive]";
    }

}
