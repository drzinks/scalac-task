package com.drzinks.scalactask.connector;

import com.drzinks.scalactask.exception.MalformedLinkHeader;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;

public class GitHubConnectorTest {

    private GitHubConnector gitHubConnector;

    @Before
    public void setup(){
        gitHubConnector = new GitHubConnector();
    }

    @Test
    public void testHasNextPageTrue() throws MalformedLinkHeader {
        String input = "<https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=2>; rel=\"next\"," +
                       " <https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=64>; rel=\"last\"";

        boolean hasNext = gitHubConnector.hasNextPage(Arrays.asList(input));
        assertEquals(true,hasNext);
    }

    @Test
    public void testHasNextPageFalse() throws MalformedLinkHeader {
        String input = "<https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=63>; rel=\"prev\"," +
                " <https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=1>; rel=\"first\"";

        boolean hasNext = gitHubConnector.hasNextPage(Arrays.asList(input));
        assertEquals(false,hasNext);
    }

    @Test(expected = MalformedLinkHeader.class)
    public void testHasNextPageMalformedHeader2() throws MalformedLinkHeader {
        gitHubConnector.hasNextPage(null);
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