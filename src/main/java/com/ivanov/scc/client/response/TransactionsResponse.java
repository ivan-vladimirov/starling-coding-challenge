package com.ivanov.scc.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivanov.scc.model.FeedItem;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionsResponse {
    @JsonProperty("feedItems")
    private List<FeedItem> feedItems;

    public List<FeedItem> getFeedItems() {
        return feedItems;
    }

    public void setFeedItems(List<FeedItem> feedItems) {
        this.feedItems = feedItems;
    }
}
