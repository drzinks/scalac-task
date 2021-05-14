package com.drzinks.scalactask.service;

import com.drzinks.scalactask.connector.GitHubConnector;
import com.drzinks.scalactask.model.Contributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContributorsService {

    @Autowired
    GitHubConnector gitHubConnector;

    public List<Contributor> getContributors(String orgName){
        List<String> contributorsUrlList = gitHubConnector.getRepositoryContributorUrlsPerOrg(orgName);
        return null;
    }

}