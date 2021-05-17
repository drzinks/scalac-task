package com.drzinks.scalactask.connector;

import com.drzinks.scalactask.exception.MalformedLinkHeader;
import com.drzinks.scalactask.exception.RestTemplateResponseErrorHandler;
import com.drzinks.scalactask.model.GitHubRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
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
    public static final String LINK_SEPARATION = ",";
    public static final int FIRST_PAGE = 1;
    @Value("${github.token}")
    private String authToken;
    @Value("${github.api.url}")
    private String apiBaseUrl;
    @Value("${github.pagesize}")
    private int pageSize;

    @Autowired
    private RestTemplate restTemplate;

    public List<String> getRepositoryContributorUrlsPerOrg(String orgName) throws MalformedLinkHeader {
        //TODO: make tests for RestTemplateBuilder().errorHandler 404 and so on
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler().setPath(String.format(apiBaseUrl + "orgs/%s/repos", orgName)));
        String url;
        HttpEntity<String> httpEntity = getHttpEntity();
        List<String> repositories = new ArrayList<>();
        boolean hasNext = false;
        url = String.format(apiBaseUrl + "orgs/%s/repos?page%d&per_page=%d", orgName, FIRST_PAGE, pageSize);
        List<String> links = call2ApiAndReturnResponseLinkHeader(url, httpEntity, repositories);
        if (hasNextPage(links)) {
            do {
                url = getNextPageUrl(links.get(0)); //not null - it was checked in hasNextPage method
                links = call2ApiAndReturnResponseLinkHeader(url, httpEntity, repositories);
                hasNext = hasNextPage(links);
            } while (hasNext);
        }
        return repositories;
    }

    private HttpEntity<String> getHttpEntity() {
        HttpHeaders htpHeaders = new HttpHeaders();
        htpHeaders.setBearerAuth(authToken);
        HttpEntity<String> httpEntity = new HttpEntity<String>(htpHeaders);
        return httpEntity;
    }

    private List<String> call2ApiAndReturnResponseLinkHeader(String url, HttpEntity<String> httpEntity, List<String> repositories) {
        ObjectMapper objectMapper = new ObjectMapper();
        Object[] objects;
        ResponseEntity<Object[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Object[].class);
        List<String> links = responseEntity.getHeaders().get("Link");
        objects = responseEntity.getBody();
        deserializeAndStoreInRepositoriesList(objects, objectMapper, repositories);
        return links;
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
        if (linkList != null && linkList.size() == 1) {
            String link = linkList.get(0);
            if (link!=null && link.contains(NEXT_MARKER)) {
                return true;
            } else {
                return false;
            }
        } else {
            log.warn("Cannot parse link from GitHub response");
            throw new MalformedLinkHeader();
        }
    }

    protected String getNextPageUrl(String link){
        //<https://api.github.com/organizations/139426/repos?page=2&per_page=5>; rel="next",
        // <https://api.github.com/organizations/139426/repos?page=40&per_page=5>; rel="last"
            //or
        //<https://api.github.com/organizations/139426/repos?page=1&per_page=5>; rel="prev",
        // <https://api.github.com/organizations/139426/repos?page=3&per_page=5>; rel="next",
        // <https://api.github.com/organizations/139426/repos?page=40&per_page=5>; rel="last",
        // <https://api.github.com/organizations/139426/repos?page=1&per_page=5>; rel="first"
            //or
        //<https://api.github.com/organizations/139426/repos?page=39&per_page=5>; rel="prev",
        // <https://api.github.com/organizations/139426/repos?page=1&per_page=5>; rel="first"

        for (String part : link.split(LINK_SEPARATION)) {
            if(part.contains(NEXT_MARKER)){
                return part.substring(part.indexOf("<") + 1,part.indexOf(">"));
            }
        }

        return "";
    }

}