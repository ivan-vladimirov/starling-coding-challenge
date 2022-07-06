package com.ivanov.scc.service;

import com.ivanov.scc.client.StarlingClient;
import com.ivanov.scc.client.response.Accounts;
import com.ivanov.scc.client.response.Transactions;
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

import static java.util.stream.Collectors.groupingBy;
@Service
public class RoundingService {
    private static final Logger LOG = LoggerFactory.getLogger(RoundingService.class);
    private final StarlingClient client;

    @Autowired
    public RoundingService(StarlingClient client) {
        this.client = client;
    }

    public List<Amount> roundUpTransactionsForAccount() {
        List<Amount> roundUps = new ArrayList<>();
        Accounts accounts = client.getAllAccounts();
        ZonedDateTime weekAgo = ZonedDateTime.parse(ZonedDateTime.now().minusDays(7).format(DateTimeFormatter.ISO_DATE_TIME));
        accounts.getAccounts().forEach( account -> roundUps.add(calculateRoundUps(account.getCurrency(), client.getTransactionsForAccount(account,weekAgo).getFeedItems())));

        return roundUps;
    }

    public Amount calculateRoundUps(String currency, List<FeedItem> feedItems) {
        Amount roundedForAccount = new Amount(currency,BigDecimal.ZERO);
        feedItems.stream().filter(e -> e.getDirection() == Direction.OUT)
                .map(FeedItem::getAmount).filter(item -> item.getCurrency().equals(currency))
                .forEach(item -> roundedForAccount.setMinorUnits(
                        roundedForAccount.getMinorUnits().add(this.roundUpItem(item.getMinorUnits()))
                        ));
        return roundedForAccount;

    }

    private BigDecimal roundUpItem(final BigDecimal item) {
        return item.setScale(-2, RoundingMode.UP).subtract(item);
    }
}
