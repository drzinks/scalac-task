package com.drzinks.scalactask.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ApiError {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss.SSSZ")
    private ZonedDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}