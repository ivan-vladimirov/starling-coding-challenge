package com.ivanov.scc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivanov.scc.common.Direction;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedItem {
    @JsonProperty("feedItemUid")
    private String feedItemUid;
    @JsonProperty("direction")
    private Direction direction;
    @JsonProperty("amount")
    private Amount amount;

    public String getFeedItemUid() {
        return feedItemUid;
    }

    public void setFeedItemUid(String feedItemUid) {
        this.feedItemUid = feedItemUid;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount ammount) {
        this.amount = ammount;
    }
}
