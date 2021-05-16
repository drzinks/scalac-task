package com.drzinks.scalactask.service;

import com.drzinks.scalactask.connector.GitHubConnector;
import com.drzinks.scalactask.model.Contributor;
import com.drzinks.scalactask.model.GitHubRepositoryNarrowedModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class ContributorsService {

    @Autowired
    GitHubConnector gitHubConnector;

    public List<GitHubRepositoryNarrowedModel> getContributors(String orgName){
        List<GitHubRepositoryNarrowedModel> contributorsUrlList = gitHubConnector.getRepositoryContributorUrlsPerOrg(orgName);
        return null;
    }

}