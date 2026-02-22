package tech.stockquotes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tech.stockquotes.domain.StockQuote;
import tech.stockquotes.dto.FinnhubQuoteResponse;
import tech.stockquotes.repository.StockQuoteRepository;
import tech.stockquotes.service.client.FinnhubClientImpl;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class StockQuoteServiceTest extends BaseIntegrationTest {

    @Autowired
    private StockQuoteService stockQuoteService;

    @Autowired
    private StockQuoteRepository stockQuoteRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @MockitoBean
    private FinnhubClientImpl finnhubClient;

    @BeforeEach
    void setUp() {
        stockQuoteRepository.deleteAll();
        assertThat(redisTemplate.getConnectionFactory()).isNotNull();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();

        when(finnhubClient.fetchQuote(anyString())).thenReturn(mockResponse());
    }

    @Test
    void shouldFetchSaveAndPersistQuote() {
        StockQuote result = stockQuoteService.getQuote("AAPL");

        assertThat(result).isNotNull();
        assertThat(result.getSymbol()).isEqualTo("AAPL");

        assertThat(stockQuoteRepository.findAll())
                .hasSize(1)
                .first()
                .satisfies(quote -> assertThat(quote.getSymbol()).isEqualTo("AAPL"));
    }

    @Test
    void shouldPersistMultipleQuotes() {
        stockQuoteService.getQuote("AAPL");
        stockQuoteService.getQuote("MSFT");

        assertThat(stockQuoteRepository.findAll()).hasSize(2);

        verify(finnhubClient, times(2)).fetchQuote(anyString());
        verify(finnhubClient).fetchQuote("AAPL");
        verify(finnhubClient).fetchQuote("MSFT");
    }

    @Test
    void shouldReturnCachedQuoteWithoutHittingDatabase() {
        String symbol = "AAPL";

        StockQuote first = stockQuoteService.getQuote(symbol);

        verify(finnhubClient, times(1)).fetchQuote(symbol);

        clearInvocations(finnhubClient);

        StockQuote second = stockQuoteService.getQuote(symbol);

        verify(finnhubClient, never()).fetchQuote(symbol);
        verifyNoMoreInteractions(finnhubClient);

        assertThat(second).isNotNull();
        assertThat(second.getSymbol()).isEqualTo(symbol);

        assertThat(stockQuoteRepository.findAll()).hasSize(1);

        var cache = cacheManager.getCache("quotes");
        assertThat(cache).isNotNull();
        assertThat(cache.get(symbol)).isNotNull();
    }

    private FinnhubQuoteResponse mockResponse() {
        return new FinnhubQuoteResponse(
                new BigDecimal("260.58"),
                new BigDecimal("-3.77"),
                new BigDecimal("-1.4261"),
                new BigDecimal("264.48"),
                new BigDecimal("260.05"),
                new BigDecimal("262.60"),
                new BigDecimal("264.35"),
                1708534800L
        );
    }
}