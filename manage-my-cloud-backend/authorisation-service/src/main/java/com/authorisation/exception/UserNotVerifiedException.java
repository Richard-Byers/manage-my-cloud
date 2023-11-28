package com.authorisation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotVerifiedException extends RuntimeException {

    private HttpStatus status;

    public UserNotVerifiedException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
