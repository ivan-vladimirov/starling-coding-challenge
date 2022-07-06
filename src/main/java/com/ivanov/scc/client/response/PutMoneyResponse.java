package com.ivanov.scc.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PutMoneyResponse {
    @JsonProperty("transferUid")
    private String transferUid;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("errors")
    private List<String> errors;
}
