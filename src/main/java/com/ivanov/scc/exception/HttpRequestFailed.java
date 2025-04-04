package com.ivanov.scc.exception;

public class HttpRequestFailed extends RuntimeException {
    public HttpRequestFailed(String message, Throwable cause) {
        super(message,cause);
    }
}
