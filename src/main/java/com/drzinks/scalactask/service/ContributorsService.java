package com.drzinks.scalactask.service;

import com.drzinks.scalactask.connector.GitHubConnector;
import com.drzinks.scalactask.model.Contributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ContributorsService {

    @Autowired
    GitHubConnector gitHubConnector;

    public List<Contributor> getContributors(String orgName){
        List<String> contributorsUrlList = gitHubConnector.getRepositoryContributorUrlsPerOrg(orgName);
        List<Contributor> contributors = new LinkedList<>();
        Map<String,Contributor> contributorsMap = new HashMap<>();
        for (String url : contributorsUrlList) {
            contributors.addAll(gitHubConnector.getContributors(url));
            contributors
                    .stream()
                    .forEach(contributor -> {
                        String contributorName = contributor.getName();
                        if(contributorsMap.containsKey(contributorName)){
                            Contributor legacyContributor = contributorsMap.get(contributorName);
                            int newContributions = legacyContributor.getContributions() + contributor.getContributions();
                            legacyContributor.setContributions(newContributions);
                            contributorsMap.put(contributorName,legacyContributor);
                        }else{
                            contributorsMap.put(contributorName,contributor);
                        }
                    });
            contributors.clear();
        }
        contributors.addAll(contributorsMap.values());
        Collections.sort(contributors);
        return contributors;
    }

}