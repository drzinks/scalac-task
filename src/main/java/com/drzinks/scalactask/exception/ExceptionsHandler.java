package com.drzinks.scalactask.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice()
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) //it is due to Spring always handled Throwables
public class ExceptionsHandler {

    @ExceptionHandler(MalformedLinkHeader.class)
    public ResponseEntity<String> handleMalformedUrlException(Throwable ex) {
        return new ResponseEntity<String>("GitHub api is failing.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GitHubApiException.class)
    public ResponseEntity<ApiError> handleGitHubApiException(GitHubApiException e) {
        log.error(e.getApiError().toString());
        return new ResponseEntity<ApiError>(e.getApiError(), HttpStatus.valueOf(e.getApiError().getStatus()));
    }

}