package com.drzinks.scalactask.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BadUrlController {
    @GetMapping(value = "/**")
    public ResponseEntity<String> badUrl(){
        log.debug("Bad url was requested.");
        return new ResponseEntity<String>("No such endpoint.", HttpStatus.NOT_FOUND);
    }
}