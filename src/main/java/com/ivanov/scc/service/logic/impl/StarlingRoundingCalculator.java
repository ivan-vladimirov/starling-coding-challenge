package com.ivanov.scc.service.logic.impl;

import com.ivanov.scc.common.Direction;
import com.ivanov.scc.model.Amount;
import com.ivanov.scc.model.FeedItem;
import com.ivanov.scc.service.logic.RoundingCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class StarlingRoundingCalculator implements RoundingCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(StarlingRoundingCalculator.class);

    @Override
    public Amount calculateRoundUps(String currency, List<FeedItem> feedItems) {
        if (feedItems == null || feedItems.isEmpty()) {
            return new Amount(currency, BigDecimal.ZERO);
        }

        BigDecimal total = BigDecimal.ZERO;

        for (FeedItem item : feedItems) {
            if (item.getDirection() == Direction.OUT &&
                    item.getAmount().getCurrency().equals(currency)) {
                BigDecimal original = item.getAmount().getMinorUnits();
                BigDecimal rounded = roundUpItem(original);
                LOG.debug("Rounding transaction: original={} -> roundUp={}", original, rounded);
                total = total.add(rounded);
            }
        }

        return new Amount(currency, total);
    }
    /**
     * Calculates the round-up amount needed to bring the transaction amount
     * up to the next whole major currency unit (e.g., £4.35 → £5.00 → returns 65).
     *
     * Starling API uses minor units (e.g., pence for GBP), so we:
     * 1. Convert to major units
     * 2. Round up to the next whole number
     * 3. Convert the difference back to minor units
     *
     * @param minorUnits The transaction amount in minor units (e.g., 435 for £4.35)
     * @return The round-up amount in minor units (e.g., 65)
     */
    private BigDecimal roundUpItem(BigDecimal minorUnits) {
        BigDecimal major = minorUnits.divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY);
        BigDecimal rounded = major.setScale(0, RoundingMode.UP);
        return rounded.subtract(major).multiply(BigDecimal.valueOf(100));
    }

}
