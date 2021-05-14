package com.drzinks.scalactask.connector;

import com.drzinks.scalactask.model.GitHubRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class GitHubConnector {

    @Value("${github.token}")
    private String authToken;
    @Value("${github.api.url}")
    private String apiBaseUrl;
    private RestTemplate restTemplate;

    //https://api.github.com/
    //https://api.github.com/orgs/adobe/repos
    //https://api.github.com/repos/adobe/brackets-app/contributors

    public List<String> getRepositoryNamesPerOrg(String orgName) {
        //TODO: use new RestTemplateBuilder().errorHandler
        //TODO: add pagination handling
        restTemplate = new RestTemplate();
        String url = apiBaseUrl + "orgs/adobe/repos";
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap(1);
        List<String> headersValues = new ArrayList<>();
        headersValues.add("Token " + authToken);
        headers.put("Authorization", headersValues);
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
        Object[] objects;
        objects = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Object[].class).getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> repositories = Arrays.stream(objects)
                .map(object -> objectMapper.convertValue(object, GitHubRepository.class))
                .map(GitHubRepository::getName)
                .collect(Collectors.toList());
        return repositories;
    }

}