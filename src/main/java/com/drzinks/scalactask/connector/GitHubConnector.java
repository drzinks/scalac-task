package com.drzinks.scalactask.connector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Repository
public class GitHubConnector {

    @Value("${github.token}")
    private String authToken;
    @Value("${github.api.url}")
    private String apiBaseUrl;
    @Value("${github.pagesize}")
    private int pageSize;

    protected WebClient webClient;

    //https://api.github.com/
    //https://api.github.com/orgs/adobe/repos
    //https://api.github.com/repos/adobe/brackets-app/contributors

    public List<String> getRepositoryContributorUrlsPerOrg(String orgName) {
        //TODO: use new RestTemplateBuilder().errorHandler 404 and so on
        //TODO: add pagination handling

        webClient = WebClient
                .builder()
                .baseUrl(apiBaseUrl)
                .defaultHeader("Authorization","Bearer " + authToken)
                .build();
        //https://api.github.com/orgs/adobe/repos?page1&per_page=100

        webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("orgs/",orgName,"/repos")
                        .queryParam("page",1)
                        .queryParam("per_page",pageSize)
                        .build());
        return null;

    }

}