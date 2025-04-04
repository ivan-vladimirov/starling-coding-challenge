package com.ivanov.scc;

import com.ivanov.scc.client.StarlingClient;
import com.ivanov.scc.client.response.Accounts;
import com.ivanov.scc.client.response.Transactions;
import com.ivanov.scc.client.HttpClient;
import com.ivanov.scc.model.Account;
import com.ivanov.scc.model.Amount;
import com.ivanov.scc.common.Direction;
import com.ivanov.scc.model.FeedItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class RoundingServiceTest {
    private RoundingService roundingService;
    @Captor
    ArgumentCaptor<ZonedDateTime> zonedDateTimeArgumentCaptor;
    @Captor
    ArgumentCaptor<String> uriCaptor;
    @Mock
    HttpClient httpClient;

    @BeforeEach
    void setup(){
        StarlingClient starlingClient = new StarlingClient(httpClient);
        roundingService = new RoundingService(starlingClient);
        Account gbp = new Account();
        gbp.setCurrency("GBP");
        gbp.setAccountUid("testing");

        Account eur = new Account();
        eur.setCurrency("EUR");

        List<Account> accounts = new ArrayList<>();
        accounts.add(gbp);
        accounts.add(eur);

        Accounts accountResponse = new Accounts();
        accountResponse.setAccounts(accounts);
        lenient().when(httpClient.sendGetWithJsonResponse(eq("/api/v2/accounts"), eq(Accounts.class)))
                .thenReturn(accountResponse);
    }

    @Test
    void testRoundingForTransactions(){
        Transactions ts = new Transactions();
        FeedItem fi1 = new FeedItem();
        fi1.setDirection(Direction.OUT);
        fi1.setAmount(new Amount("GBP", BigDecimal.valueOf(550)));
        FeedItem fi2 = new FeedItem();
        fi2.setDirection(Direction.OUT);
        fi2.setAmount(new Amount("GBP", BigDecimal.valueOf(585)));

        List<FeedItem> feedItems = new ArrayList<>();
        feedItems.add(fi1);
        feedItems.add(fi2);

        ts.setFeedItems(feedItems);

        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(Transactions.class)))
                .thenReturn(ts);

        Amount gbp = roundingService.roundUpTransactionsForAllAccount().stream()
                .filter(item -> item.getCurrency().equals("GBP"))
                .findAny().orElse(null);
        assert gbp != null;
        assertEquals(BigDecimal.valueOf(65), gbp.getMinorUnits());
    }

    @Test
    void testRoundingForTransactionsWithEvenTransactions(){
        Transactions ts = new Transactions();

        FeedItem fi1 = new FeedItem();
        fi1.setDirection(Direction.OUT);
        fi1.setAmount(new Amount("GBP", BigDecimal.valueOf(500)));
        FeedItem fi2 = new FeedItem();
        fi2.setDirection(Direction.OUT);
        fi2.setAmount(new Amount("GBP", BigDecimal.valueOf(522)));
        FeedItem fi3 = new FeedItem();
        fi3.setDirection(Direction.OUT);
        fi3.setAmount(new Amount("GBP", BigDecimal.valueOf(111)));

        List<FeedItem> feedItems = new ArrayList<>();
        feedItems.add(fi1);
        feedItems.add(fi2);
        feedItems.add(fi3);

        ts.setFeedItems(feedItems);

        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(Transactions.class)))
                .thenReturn(ts);
        Amount gbp = roundingService.roundUpTransactionsForAllAccount().stream()
                .filter(item -> item.getCurrency().equals("GBP"))
                .findAny().orElse(null);
        assert gbp != null;
        assertEquals(BigDecimal.valueOf(167), gbp.getMinorUnits());
    }
    @Test
    void testRoundingForTransactionsWithAllInDirection(){
        Transactions ts = new Transactions();

        FeedItem fi1 = new FeedItem();
        fi1.setDirection(Direction.IN);
        fi1.setAmount(new Amount("GBP", BigDecimal.valueOf(500)));
        FeedItem fi2 = new FeedItem();
        fi2.setDirection(Direction.IN);
        fi2.setAmount(new Amount("GBP", BigDecimal.valueOf(522)));
        FeedItem fi3 = new FeedItem();
        fi3.setDirection(Direction.IN);
        fi3.setAmount(new Amount("GBP", BigDecimal.valueOf(111)));

        List<FeedItem> feedItems = new ArrayList<>();
        feedItems.add(fi1);
        feedItems.add(fi2);
        feedItems.add(fi3);

        ts.setFeedItems(feedItems);

        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(Transactions.class)))
                .thenReturn(ts);
        Amount gbp = roundingService.roundUpTransactionsForAllAccount().stream()
                .filter(item -> item.getCurrency().equals("GBP"))
                .findAny().orElse(null);
        assert gbp != null;
        assertEquals(BigDecimal.ZERO, gbp.getMinorUnits());
    }
    @Test
    void testRoundingWithTranasctionsProvidedByStarling(){
        Transactions ts = new Transactions();

        FeedItem fi1 = new FeedItem();
        fi1.setDirection(Direction.OUT);
        fi1.setAmount(new Amount("GBP", BigDecimal.valueOf(435)));
        FeedItem fi2 = new FeedItem();
        fi2.setDirection(Direction.OUT);
        fi2.setAmount(new Amount("GBP", BigDecimal.valueOf(520)));
        FeedItem fi3 = new FeedItem();
        fi3.setDirection(Direction.OUT);
        fi3.setAmount(new Amount("GBP", BigDecimal.valueOf(87)));

        List<FeedItem> feedItems = new ArrayList<>();
        feedItems.add(fi1);
        feedItems.add(fi2);
        feedItems.add(fi3);

        ts.setFeedItems(feedItems);

        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(Transactions.class)))
                .thenReturn(ts);
        Amount gbp = roundingService.roundUpTransactionsForAllAccount().stream()
                .filter(item -> item.getCurrency().equals("GBP"))
                .findAny().orElse(null);
        assert gbp != null;
        assertEquals(BigDecimal.valueOf(158), gbp.getMinorUnits());
    }

    @Test
    void testRoundingWithDifferentCurrencies(){
        Transactions ts = new Transactions();

        FeedItem fi1 = new FeedItem();
        fi1.setDirection(Direction.OUT);
        fi1.setAmount(new Amount("GBP", BigDecimal.valueOf(435)));
        FeedItem fi2 = new FeedItem();
        fi2.setDirection(Direction.OUT);
        fi2.setAmount(new Amount("EUR", BigDecimal.valueOf(520)));
        FeedItem fi3 = new FeedItem();
        fi3.setDirection(Direction.OUT);
        fi3.setAmount(new Amount("GBP", BigDecimal.valueOf(87)));

        List<FeedItem> feedItems = new ArrayList<>();
        feedItems.add(fi1);
        feedItems.add(fi2);
        feedItems.add(fi3);

        ts.setFeedItems(feedItems);

        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(Transactions.class)))
                .thenReturn(ts);

        Amount gbp = roundingService.roundUpTransactionsForAllAccount().stream()
                .filter(item -> item.getCurrency().equals("GBP"))
                .findAny().orElse(null);

        Amount eur = roundingService.roundUpTransactionsForAllAccount().stream()
                .filter(item -> item.getCurrency().equals("EUR"))
                .findAny().orElse(null);

        assert gbp != null;
        assertEquals(BigDecimal.valueOf(78), gbp.getMinorUnits());
        assert eur != null;
        assertEquals(BigDecimal.valueOf(80), eur.getMinorUnits());
    }
    @Test
    void testRoundingForAccount(){
        Transactions ts = new Transactions();

        FeedItem fi1 = new FeedItem();
        fi1.setDirection(Direction.OUT);
        fi1.setAmount(new Amount("GBP", BigDecimal.valueOf(435)));


        List<FeedItem> feedItems = new ArrayList<>();
        feedItems.add(fi1);


        ts.setFeedItems(feedItems);

        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(Transactions.class)))
                .thenReturn(ts);
        StarlingClient starlingClient = new StarlingClient(httpClient);
        Account acc = starlingClient.getAccountForId("testing");
        Amount gbp = roundingService.roundUpTransactionForAccount(acc.getAccountUid());

        assert gbp != null;
        assertEquals(BigDecimal.valueOf(65), gbp.getMinorUnits());

    }
}
