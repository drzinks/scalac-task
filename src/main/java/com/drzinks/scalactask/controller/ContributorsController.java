package com.drzinks.scalactask.controller;

import com.drzinks.scalactask.connector.GitHubConnector;
import com.drzinks.scalactask.exception.MalformedLinkHeader;
import com.drzinks.scalactask.model.Contributor;
import com.drzinks.scalactask.service.ContributorsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@Slf4j
@Validated
public class ContributorsController {
    //TODO: add hateos
    @Autowired
    ContributorsService contributorsService;

    @GetMapping(value = "/org/{orgName}/contributors")
    public List<String> getContributors(@PathVariable(value = "orgName") String orgName) throws MalformedLinkHeader {
        log.info("Request for " + orgName +" was made.");
        return contributorsService.getContributors(orgName);
    }

}