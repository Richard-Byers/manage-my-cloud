package com.authorisation.registration.password;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String email;
    private String newPassword;
    private String confirmPassword;
}
