package com.drzinks.scalactask.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Slf4j
@Validated
public class ContributorsController {

    @GetMapping(value = "/org/{orgName}/contributors")
    public String getContributors(@PathVariable(value = "orgName") String orgName){
        log.info("Request for " + orgName +" was made.");
        return orgName;
    }

}