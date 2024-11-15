package com.example.currencyexchange.errorhandling;

import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

@Getter
public class NBPIntegrationException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final HttpHeaders headers;

    public NBPIntegrationException(HttpStatusCode statusCode, HttpHeaders headers) {
        super(String.format("HTTP request failed with status code: %s", statusCode));
        this.statusCode = statusCode;
        this.headers = headers;
    }

    public NBPIntegrationException(HttpStatusCode statusCode, HttpHeaders headers, String message) {
        super(message);
        this.statusCode = statusCode;
        this.headers = headers;
    }

    public NBPIntegrationException(HttpStatusCode statusCode, HttpHeaders headers, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.headers = headers;
    }
}
