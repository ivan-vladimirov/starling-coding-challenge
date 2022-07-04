package com.ivanov.scc;

import com.ivanov.scc.client.StarlingClient;
import com.ivanov.scc.client.response.AccountResponse;
import com.ivanov.scc.client.response.TransactionsResponse;
import com.ivanov.scc.config.HttpClient;
import com.ivanov.scc.model.Account;
import com.ivanov.scc.model.Amount;
import com.ivanov.scc.model.Direction;
import com.ivanov.scc.model.FeedItem;
import com.ivanov.scc.service.RoundingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
        Account acc = new Account();
        List<Account> accounts = new ArrayList<>();
        accounts.add(acc);

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setAccounts(accounts);
        lenient().when(httpClient.sendGetWithJsonResponse(eq("/api/v2/accounts"), eq(AccountResponse.class)))
                .thenReturn(accountResponse);

    }

    @Test
    void testRoundingForTransactions(){
        TransactionsResponse ts = new TransactionsResponse();

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

        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(TransactionsResponse.class)))
                .thenReturn(ts);
        assertEquals(BigDecimal.valueOf(0.65), roundingService.roundUpTransactions().get("GBP"));
    }

    @Test
    void testRoundingForTransactionsWithEvenTransactions(){
        TransactionsResponse ts = new TransactionsResponse();

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

        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(TransactionsResponse.class)))
                .thenReturn(ts);
        assertEquals(BigDecimal.valueOf(1.67), roundingService.roundUpTransactions().get("GBP"));
    }
    @Test
    void testRoundingForTransactionsWithAllInDirection(){
        TransactionsResponse ts = new TransactionsResponse();

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

        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(TransactionsResponse.class)))
                .thenReturn(ts);
        assertEquals(BigDecimal.valueOf(0), roundingService.roundUpTransactions().get("GBP"));
    }
    @Test
    void testRoundingWithTranasctionsProvidedByStarling(){
        TransactionsResponse ts = new TransactionsResponse();

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

        lenient().when(httpClient.sendGetWithJsonResponse(uriCaptor.capture(), eq(TransactionsResponse.class)))
                .thenReturn(ts);
        assertEquals(BigDecimal.valueOf(1.58), roundingService.roundUpTransactions().get("GBP"));
    }
}
