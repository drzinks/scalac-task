package com.drzinks.scalactask.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice()
@Slf4j
/* handle Throwables that can occur starting from getContributors processing */
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleException(Throwable ex) {
        log.error("Unexpected exception occured", ex);
        return new ResponseEntity<String>("Sorry, unexpected error occured, we'll try to fix it.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
