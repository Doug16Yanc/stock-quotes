package tech.stockquotes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tech.stockquotes.dto.FinnhubQuoteResponse;

import java.math.BigDecimal;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FinnhubClientTest extends BaseIntegrationTest {

    @Autowired
    private FinnhubClient finnhubClient;

    @MockitoBean
    private WebClient finnhubWebClient;

    @MockitoBean
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @MockitoBean
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @MockitoBean
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        when(finnhubWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(FinnhubQuoteResponse.class)).thenReturn(Mono.just(mockResponse()));
    }

    @Test
    void shouldFetchQuoteFromApi() {
        FinnhubQuoteResponse result = finnhubClient.fetchQuote("AAPL");

        assertThat(result).isNotNull();
        assertThat(result.currentPrice()).isEqualByComparingTo("260.58");
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