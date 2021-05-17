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
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();


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
    public void testHasNextPageMalformedHeader() throws MalformedLinkHeader {
        String input = "sul8r";
        gitHubConnector.hasNextPage(Arrays.asList(input));
    }

    @Test(expected = MalformedLinkHeader.class)
    public void testHasNextPageMalformedHeader2() throws MalformedLinkHeader {
        gitHubConnector.hasNextPage(null);
    }

    @Test
    public void testGetLastPageFound() throws MalformedLinkHeader {
        /*two digit last page*/
        String input = "<https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=2>; rel=\"next\"," +
                " <https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=64>; rel=\"last\"";

        int lastpage = gitHubConnector.getLastPage(Arrays.asList(input));
        assertEquals(64,lastpage);
    }

    @Test
    public void testGetLastPageFound2() throws MalformedLinkHeader {
        /*one digit last page*/
        String input = "<https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=2>; rel=\"next\"," +
                " <https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=6>; rel=\"last\"";

        int lastpage = gitHubConnector.getLastPage(Arrays.asList(input));
        assertEquals(6,lastpage);
    }

    @Test
    public void testGetLastPageNotFound() throws MalformedLinkHeader {
        String input = "<https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=63>; rel=\"prev\"," +
                " <https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=1>; rel=\"first\"";
        int lastpage = gitHubConnector.getLastPage(Arrays.asList(input));
        assertEquals(0,lastpage);
    }

    @Test(expected = MalformedLinkHeader.class)
    public void testGetLastPageMalformedHeader1() throws MalformedLinkHeader {
        String input = "s/476009/repos?page1=&per_page=10&page=1>; rel=\"first\"";
        gitHubConnector.getLastPage(Arrays.asList(input," df"));
    }

    @Test(expected = MalformedLinkHeader.class)
    public void testGetLastPageMalformedHeader2() throws MalformedLinkHeader {
        String input = "<https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=2>; rel=\"next\"," +
                " <https://api.github.com/organizations/476009/repos?page1=&per_page=10&page=wtf>; rel=\"last\"";
        gitHubConnector.getLastPage(Arrays.asList(input));
    }

}