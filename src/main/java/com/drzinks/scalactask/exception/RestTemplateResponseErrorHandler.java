package com.drzinks.scalactask.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Slf4j
@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler{

    private String path;

    public RestTemplateResponseErrorHandler setPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
                ||
                httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        GitHubApiException gitHubApiException = new GitHubApiException();
        if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            gitHubApiException.setApiError(new ApiError(ZonedDateTime.now(),HttpStatus.INTERNAL_SERVER_ERROR.value(),"GitHub Api does not work :(",
                    "No message available",path));
            throw gitHubApiException;
        } else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                gitHubApiException.setApiError(new ApiError(ZonedDateTime.now(),HttpStatus.NOT_FOUND.value(),"No such organization",
                        "No message available",path));
                throw gitHubApiException;
            }else {
                log.error("Error connecting GitHub Api - client error, httpResponse details:");
                log.error(httpResponse.getStatusText());
                log.error(httpResponse.getHeaders().toString());
                log.error(httpResponse.getBody().toString());
                gitHubApiException.setApiError(new ApiError(ZonedDateTime.now(),HttpStatus.INTERNAL_SERVER_ERROR.value(),"Sorry, unexpected error occured, we'll try to fix it.",
                        "No message available",path));
                throw gitHubApiException;
            }
        }
    }
}
