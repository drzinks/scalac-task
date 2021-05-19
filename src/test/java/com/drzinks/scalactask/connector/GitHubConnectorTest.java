package com.drzinks.scalactask.connector;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class GitHubConnectorTest {

    private GitHubConnector gitHubConnector;

    @Before
    public void setup(){
        gitHubConnector = new GitHubConnector();
    }

    @Test
    public void testHasNextPageTrue(){
        String input = "<https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=2>; rel=\"next\"," +
                       " <https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=64>; rel=\"last\"";

        boolean hasNext = gitHubConnector.hasNextPage(Arrays.asList(input));
        assertEquals(true,hasNext);
    }

    @Test
    public void testHasNextPageFalse(){
        String input = "<https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=63>; rel=\"prev\"," +
                " <https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=1>; rel=\"first\"";

        boolean hasNext = gitHubConnector.hasNextPage(Arrays.asList(input));
        assertEquals(false,hasNext);
    }

    @Test
    public void testgetNextPageUrl() {
        String nextPage = gitHubConnector.getNextPageUrl("<https://api.github.com/organizations/139426/repos?page=1&per_page=5>; rel=\"prev\"," +
                " <https://api.github.com/organizations/139426/repos?page=3&per_page=5>; rel=\"next\"," +
                " <https://api.github.com/organizations/139426/repos?page=40&per_page=5>; rel=\"last\"," +
                " <https://api.github.com/organizations/139426/repos?page=1&per_page=5>; rel=\"first\"");
        assertEquals("https://api.github.com/organizations/139426/repos?page=3&per_page=5",nextPage);
    }

    @Test
    public void testgetNextPageUrlNotFound() {
        String nextPage = gitHubConnector.getNextPageUrl("<https://api.github.com/organizations/139426/repos?page=39&per_page=5>; rel=\"prev\"," +
                " <https://api.github.com/organizations/139426/repos?page=1&per_page=5>; rel=\"first\"");
        assertEquals("",nextPage);
    }

    @Test
    public void testgetNextPageUrlNotFound2() {
        String nextPage = gitHubConnector.getNextPageUrl("***** ***");
        assertEquals("",nextPage);
    }

}