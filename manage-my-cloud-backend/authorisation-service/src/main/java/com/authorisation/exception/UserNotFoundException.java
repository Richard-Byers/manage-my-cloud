package com.authorisation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotFoundException extends RuntimeException {

    private HttpStatus status = HttpStatus.NOT_FOUND;

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
