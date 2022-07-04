package com.ivanov.scc.config;


public class HttpNoOkResponse extends RuntimeException {
    private final String request;
    private final String responseBody;
    private final int rawCode;
    private final HttpCode code;

    public HttpNoOkResponse(String request, String responseBody, int code) {
        super(String.format("HTTP Request '%s' failed with code %s (raw code = %d) and response body: %s", request, HttpCode.fromCode(code), code, responseBody));
        this.responseBody = responseBody;
        this.rawCode = code;
        this.code = HttpCode.fromCode(code);
        this.request = request;
    }

    public String getResponseBody() {
        return this.responseBody;
    }

    public HttpCode getCode() {
        return this.code;
    }

    public int getRawCode() {
        return this.rawCode;
    }

    public String getRequest() {
        return this.request;
    }

    public String getMessage() {
        return super.getMessage();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("HttpNoOkResponse{");
        sb.append("code=").append(this.code);
        sb.append(", rawCode=").append(this.rawCode);
        sb.append(", request='").append(this.request).append('\'');
        sb.append(", body='").append(this.responseBody).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
