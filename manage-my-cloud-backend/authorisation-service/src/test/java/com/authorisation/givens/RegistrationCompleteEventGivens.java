package com.authorisation.givens;

import com.authorisation.event.RegistrationCompleteEvent;

import static com.authorisation.givens.UserEntityGivens.generateUserEntity;

public class RegistrationCompleteEventGivens {

    public static RegistrationCompleteEvent generateRegistrationCompleteEvent() {
        return new RegistrationCompleteEvent(
                generateUserEntity(),
                "http://localhost:8080"
        );
    }

}
