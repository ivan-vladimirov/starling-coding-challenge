package com.ivanov.scc.service.logic;

import com.ivanov.scc.model.Amount;
import com.ivanov.scc.model.FeedItem;

import java.util.List;

public interface RoundingCalculator {

    /**
     * Calculates the total round-up from a list of feed items in a specific currency.
     *
     * @param currency   The currency to filter by.
     * @param feedItems  A list of transaction feed items.
     * @return           An Amount object representing the total round-up.
     */
    Amount calculateRoundUps(String currency, List<FeedItem> feedItems);
}

