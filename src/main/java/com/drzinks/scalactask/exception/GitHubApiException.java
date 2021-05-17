package com.drzinks.scalactask.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Getter
@Setter
public class GitHubApiException extends IOException {
    private ApiError apiError;
}