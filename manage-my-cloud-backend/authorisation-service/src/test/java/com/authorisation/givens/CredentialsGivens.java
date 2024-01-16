package com.authorisation.givens;

import com.authorisation.dto.CredentialsDto;

public class CredentialsGivens {

    public static CredentialsDto generateCredentialsDto() {
        return CredentialsDto.builder()
                .email("email").password("password").build();
    }

}
