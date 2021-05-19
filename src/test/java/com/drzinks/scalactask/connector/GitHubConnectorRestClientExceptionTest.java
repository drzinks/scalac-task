//package com.drzinks.scalactask.connector;
//
//import com.drzinks.scalactask.App;
//import com.drzinks.scalactask.exception.GitHubApiException;
//import com.drzinks.scalactask.exception.MalformedLinkHeader;
//import com.drzinks.scalactask.exception.RestTemplateResponseErrorHandler;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.client.ExpectedCount;
//import org.springframework.test.web.client.MockRestServiceServer;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.TimeZone;
//
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
//import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
//import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
//
//@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = {App.class})
//@RestClientTest
//public class GitHubConnectorRestClientExceptionTest {
//
//    public static final String URL = "https://api.github.com/orgs/angular/repos";
//
//    @Autowired
//    private RestTemplate restTemplate;
//    private MockRestServiceServer server;
//    @Autowired
//    private GitHubConnector gitHubConnector;
//
//    @Before
//    public void setup() {
//        server = MockRestServiceServer.createServer(restTemplate);
//    }
//
//    @Test()
//    public void testNotFound() throws MalformedLinkHeader {
//        server.expect(ExpectedCount.once(), requestTo(URL))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(withStatus(HttpStatus.NOT_FOUND));
//
//        try {
//            gitHubConnector.getRepositoryContributorUrlsPerOrg("angular");
//        } catch (RestClientException e) {
//            Assert.assertTrue(e.getCause() instanceof GitHubApiException);
//            GitHubApiException gitHubApiException = (GitHubApiException) e.getCause();
//            Assert.assertEquals(gitHubApiException.getApiError().getError(), "No such user/repo");
//            Assert.assertEquals(gitHubApiException.getApiError().getMessage(), "No message available");
//            Assert.assertEquals(gitHubApiException.getApiError().getPath(), URL);
//            Assert.assertEquals(gitHubApiException.getApiError().getStatus(), HttpStatus.NOT_FOUND.value());
//        }
//        server.verify();
//
//    }
//
//}