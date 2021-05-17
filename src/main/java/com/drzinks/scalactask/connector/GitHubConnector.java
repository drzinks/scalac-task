package com.drzinks.scalactask.connector;

import com.drzinks.scalactask.exception.MalformedLinkHeader;
import com.drzinks.scalactask.exception.RestTemplateResponseErrorHandler;
import com.drzinks.scalactask.model.GitHubRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class GitHubConnector {

    public static final String PAGE_MARKER = "&page=";
    public static final String NEXT_MARKER = "rel=\"next\"";
    public static final String LAST_MARKER = "rel=\"last\"";
    public static final String PREVIOUS_MARKER = "rel=\"prev\"";
    public static final String FIRST_MARKER = "rel=\"first\"";
    @Value("${github.token}")
    private String authToken;
    @Value("${github.api.url}")
    private String apiBaseUrl;
    @Value("${github.pagesize}")
    private int pageSize;

    private RestTemplate restTemplate;
    
    public List<String> getRepositoryContributorUrlsPerOrg(String orgName) throws MalformedLinkHeader {
        //TODO: make tests for RestTemplateBuilder().errorHandler 404 and so on
        restTemplate = new RestTemplateBuilder()
                .errorHandler(new RestTemplateResponseErrorHandler()
                        .setPath(String.format(apiBaseUrl + "orgs/%s/repos",orgName)))
                .build();
        String url;
        HttpHeaders htpHeaders = new HttpHeaders();
        htpHeaders.setBearerAuth(authToken);
        HttpEntity<String> httpEntity = new HttpEntity<String>(htpHeaders);
        Object[] objects;
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> repositories = new ArrayList<>();
        int currentPage = 1;
        int lastPage = 0;
        boolean hasNext = true;
        do{
            // TODO: fetch url for 2+ pages from received link header in future
            url = String.format(apiBaseUrl + "orgs/%s/repos?page%d&per_page=%d",orgName,currentPage,pageSize);
            ResponseEntity<Object[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Object[].class);
            List<String> links = responseEntity.getHeaders().get("Link");
            objects = responseEntity.getBody();
            deserializeAndStoreInRepositoriesList(objects, objectMapper, repositories);
            hasNext = hasNextPage(links);
            if(currentPage == 1){
                lastPage = getLastPage(links);
            }
            currentPage++;
        }while(hasNext && (currentPage <= lastPage));

        return repositories;
    }

    private void deserializeAndStoreInRepositoriesList(Object[] objects, ObjectMapper objectMapper, List<String> repositories) {
        if (objects != null && objects.length > 0) {
            repositories.addAll(
                    Arrays.stream(objects)
                            .map(object -> objectMapper.convertValue(object, GitHubRepository.class))
                            .map(GitHubRepository::getContributorsUrl)
                            .collect(Collectors.toList())
            );
        }
    }

    protected boolean hasNextPage(List<String> linkList) throws MalformedLinkHeader {
        if(linkList != null && linkList.size() == 1){
            String link = linkList.get(0);
            if(link.contains(NEXT_MARKER) && link.contains(LAST_MARKER)){
                return true;
            } else if(link.contains(PREVIOUS_MARKER) && link.contains(FIRST_MARKER)){
                return false;
            } else {
                log.warn("Cannot parse link from GitHub response");
                log.warn("Link that was received: " + link);
                throw new MalformedLinkHeader();
            }
        }else{
            log.warn("Cannot parse link from GitHub response");
            throw new MalformedLinkHeader();
        }
    }

    protected int getLastPage(List<String> linkList) throws MalformedLinkHeader {
        if(linkList != null && linkList.size() == 1){
            String link = linkList.get(0);
            if(link.contains("rel=\"last\"")){
                String[] tempArray = link.split(",");
                if(tempArray != null && tempArray.length == 2){
                    String splittedLink = tempArray[1];
                    String lastpage = splittedLink.substring(splittedLink.lastIndexOf(PAGE_MARKER)+PAGE_MARKER.length(),
                                      splittedLink.indexOf(">"));
                    try {
                        int lastpageInt = Integer.valueOf(lastpage);
                        return lastpageInt;
                    } catch(NumberFormatException e){
                        log.warn("Cannot parse link from GitHub response - while fetching last page, cannot transform to int");
                        throw new MalformedLinkHeader();
                    }
                }else{
                    log.warn("Cannot parse link from GitHub response - while fetching last page");
                    throw new MalformedLinkHeader();
                }
            }else{
                return 0; //last page not found
            }
        }else{
            log.warn("Cannot parse link from GitHub response - while fetching last page");
            throw new MalformedLinkHeader();
        }
    }


}