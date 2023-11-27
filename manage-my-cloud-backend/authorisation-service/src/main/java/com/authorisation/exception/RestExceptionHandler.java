package com.authorisation.exception;

import com.authorisation.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = {InvalidPasswordException.class})
    @ResponseBody
    public ResponseEntity<ErrorDto> handleException(InvalidPasswordException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorDto.builder().message(ex.getMessage()).build());
    }

    @ExceptionHandler(value = {UserAlreadyExistsException.class})
    @ResponseBody
    public ResponseEntity<ErrorDto> handleException(UserAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorDto.builder().message(ex.getMessage()).build());
    }

    @ExceptionHandler(value = {UserNotFoundException.class})
    @ResponseBody
    public ResponseEntity<ErrorDto> handleException(UserNotFoundException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorDto.builder().message(ex.getMessage()).build());
    }
}
