package com.authorisation.givens;

import com.authorisation.registration.RegistrationRequest;

public class RegistrationRequestGivens {

    public static RegistrationRequest generateRegistrationRequest() {
        return new RegistrationRequest(
                "John",
                "Doe",
                "johndoe@gmail.com",
                "password",
                "USER"
        );
    }


}
