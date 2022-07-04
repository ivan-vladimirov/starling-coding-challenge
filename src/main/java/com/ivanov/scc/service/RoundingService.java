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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoundingService {
    private static final Logger LOG = LoggerFactory.getLogger(RoundingService.class);
    private final StarlingClient client;

    @Autowired
    public RoundingService(StarlingClient client) {
        this.client = client;
    }

    public Map<String,BigDecimal> roundUpTransactions() {
        Map<String,BigDecimal> roundUps = new HashMap<>();
        List<TransactionsResponse> transactionsResponses = client.getTransactions(ZonedDateTime.parse(ZonedDateTime.now().minusDays(7).format(DateTimeFormatter.ISO_DATE_TIME)));
        transactionsResponses.forEach(transactionsResponse -> roundUps.put(
                transactionsResponse.getFeedItems().get(0).getAmount().getCurrency(),
                calculateRoundUps(transactionsResponse.getFeedItems()))
        );
        return roundUps;
    }

    public BigDecimal calculateRoundUps(final List<FeedItem> feedItems) {
        return feedItems.stream()
                .filter(e -> e.getDirection() == Direction.OUT)
                .map(FeedItem::getAmount)
                .map(Amount::getMinorUnits)
                .reduce(BigDecimal.ZERO, (bigDecimal, amount) -> bigDecimal.add(this.roundUpItem(amount)));
    }

    private BigDecimal roundUpItem(final BigDecimal item) {
        return item.setScale(0, RoundingMode.UP).subtract(item);
    }
}
