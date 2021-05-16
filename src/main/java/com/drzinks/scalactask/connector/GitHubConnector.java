package com.drzinks.scalactask.connector;

import com.drzinks.scalactask.model.GitHubRepositoryNarrowedModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@Slf4j
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


    public List<GitHubRepositoryNarrowedModel> getRepositoryContributorUrlsPerOrg(String orgName) {
        //TODO: use new RestTemplateBuilder().errorHandler 404 and so on
        //TODO: add pagination handling

        webClient = WebClient.builder()
                .baseUrl(apiBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.github.v3+json")
                .defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
                .filter(ExchangeFilterFunctions.basicAuthentication("drzinks",authToken))
                .filter(WebClientFilter.logRequest())
                .filter(WebClientFilter.logResponse())
//                .defaultHeader("Authorization","Bearer " + authToken)
                .build();
        //https://api.github.com/orgs/adobe/repos?page1&per_page=100



        Mono<List<GitHubRepositoryNarrowedModel>> response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/orgs/adobe/repos")
//                        .pathSegment("orgs",orgName,"repos")
                        .queryParam("page",1)
                        .queryParam("per_page",pageSize)
                        .build())
//                .headers(h -> h.setBearerAuth(authToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GitHubRepositoryNarrowedModel>>() {});

//                .bodyToMono(GitHubRepository.class);
        List<GitHubRepositoryNarrowedModel> responseList = response.block();
//        UriSpec<RequestBodySpec> uriSpec = webClient.method(HttpMethod.GET);
//        RequestBodySpec bodySpec = uriSpec.uri(uriBuilder -> uriBuilder
//                        .pathSegment("orgs/",orgName,"/repos")
//                        .queryParam("page",1)
//                        .queryParam("per_page",pageSize)
//                        .build());

        return responseList;

    }


}