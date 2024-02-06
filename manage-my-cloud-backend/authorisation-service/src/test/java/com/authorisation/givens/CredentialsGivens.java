package com.authorisation.givens;

import com.authorisation.dto.CredentialsDto;
import com.authorisation.dto.EmailDto;

public class CredentialsGivens {

    public static CredentialsDto generateCredentialsDto() {
        return CredentialsDto.builder()
                .email("email").password("password").build();
    }

    public static EmailDto generateEmailDto() {
        return EmailDto.builder()
                .email("email").build();
    }

}
