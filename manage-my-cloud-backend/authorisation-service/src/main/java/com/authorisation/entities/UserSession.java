package com.authorisation.entities;

import lombok.Getter;
import lombok.Setter;

public class UserSession {

    @Getter
    @Setter
    private String jwtToken;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String googleToken;


}
