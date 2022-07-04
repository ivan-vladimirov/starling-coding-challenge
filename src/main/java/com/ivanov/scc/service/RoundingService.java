package com.ivanov.scc.service;

import com.ivanov.scc.client.StarlingClient;
import com.ivanov.scc.client.response.TransactionsResponse;
import com.ivanov.scc.model.Account;
import com.ivanov.scc.model.Amount;
import com.ivanov.scc.model.Direction;
import com.ivanov.scc.model.FeedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoundingService {
    private static final Logger LOG = LoggerFactory.getLogger(RoundingService.class);
    private final StarlingClient client;

    @Autowired
    public RoundingService(StarlingClient client) {
        this.client = client;
    }

    public List<Map<String,BigDecimal>> roundUpTransactions() {
        List<Map<String,BigDecimal>> roundUps = new ArrayList<>();
        List<TransactionsResponse> transactionsResponses = client.
                getTransactions(ZonedDateTime.parse(ZonedDateTime.now().minusDays(7).format(DateTimeFormatter.ISO_DATE_TIME)));
        transactionsResponses.forEach(transactionsResponse -> roundUps.add(calculateRoundUps(transactionsResponse.getFeedItems()))
        );
        return roundUps;
    }

    public Map<String,BigDecimal> calculateRoundUps(final List<FeedItem> feedItems) {
        Map<String,BigDecimal> result = new HashMap<>();
        feedItems.stream()
                .filter(e -> e.getDirection() == Direction.OUT)
                .map(FeedItem::getAmount).forEach( item -> {
                    if (result.get(item.getCurrency()) == null) {
                        result.put(item.getCurrency(), this.roundUpItem(item.getMinorUnits()));
                    } else {
                        result.put(item.getCurrency(), result.get(item.getCurrency()).add(this.roundUpItem(item.getMinorUnits())));
                    }
                });

        return result;

    }

    private BigDecimal roundUpItem(final BigDecimal item) {
        return item.setScale(0, RoundingMode.UP).subtract(item);
    }
}
