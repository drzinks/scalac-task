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
    @Value("${github.pagesize}")
    private int pageSize;

    private RestTemplate restTemplate;

    //https://api.github.com/
    //https://api.github.com/orgs/adobe/repos
    //https://api.github.com/repos/adobe/brackets-app/contributors

    public List<String> getRepositoryContributorUrlsPerOrg(String orgName) {
        //TODO: use new RestTemplateBuilder().errorHandler 404 and so on
        //TODO: add pagination handling
        restTemplate = new RestTemplate();
        //?page1&per_page=100
        boolean notLastPage = true;
        int i = 1;
        String url;
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap(1);
        List<String> headersValues = new ArrayList<>();
        headersValues.add("Bearer " + authToken);
        headers.put("Authorization", headersValues);
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
        Object[] objects;
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> repositories = new ArrayList<>();
        while(notLastPage){
            url = String.format(apiBaseUrl + "orgs/%s/repos?page%d&per_page=%d",orgName,i,pageSize);
            objects = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Object[].class).getBody();
            if(objects != null && objects.length >0) {
                repositories.addAll(
                        Arrays.stream(objects)
                                .map(object -> objectMapper.convertValue(object, GitHubRepository.class))
                                .map(GitHubRepository::getContributorsUrl)
                                .collect(Collectors.toList())
                );
            } else{
                notLastPage = false;
            }
            i++;
        }

        return repositories;
    }

}