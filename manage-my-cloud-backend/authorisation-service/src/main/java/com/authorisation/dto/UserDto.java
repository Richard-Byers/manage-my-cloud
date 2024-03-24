package com.authorisation.dto;

import com.authorisation.entities.LinkedAccounts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private boolean firstLogin;
    private String email;
    private String token;
    private String refreshToken;
    private String accountType;
    private LinkedAccounts linkedAccounts;
    private byte[] profileImage;
}
