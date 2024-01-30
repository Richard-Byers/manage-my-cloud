package com.authorisation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidPasswordException extends RuntimeException {

    private HttpStatus status;

    public InvalidPasswordException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
