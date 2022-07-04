package com.ivanov.scc.config;

public class HttpRequestFailed extends RuntimeException {
    public HttpRequestFailed(String message, Throwable cause) {
        super(message,cause);
    }
}
