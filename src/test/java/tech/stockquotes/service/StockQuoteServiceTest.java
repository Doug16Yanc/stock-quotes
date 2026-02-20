package tech.stockquotes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tech.stockquotes.domain.StockQuote;
import tech.stockquotes.dto.FinnhubQuoteResponse;
import tech.stockquotes.repository.StockQuoteRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ExtendWith(MockitoExtension.class)
class StockQuoteServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("spring.data.redis.password", () -> "");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("finnhub.token", () -> "test-token");
        registry.add("finnhub.symbols", () -> "AAPL,MSFT");
        registry.add("finnhub.base-url", () -> "http://localhost");
        registry.add("finnhub.refresh-rate-ms", () -> "60000");
    }

    @Autowired
    private StockQuoteService stockQuoteService;

    @Autowired
    private StockQuoteRepository stockQuoteRepository;

    @MockitoBean
    private WebClient finnhubWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private FinnhubQuoteResponse response;

    @BeforeEach
    void setUp() {
        stockQuoteRepository.deleteAll();

        response = new FinnhubQuoteResponse(
                new BigDecimal("260.58"),
                new BigDecimal("-3.77"),
                new BigDecimal("-1.4261"),
                new BigDecimal("264.48"),
                new BigDecimal("260.05"),
                new BigDecimal("262.60"),
                new BigDecimal("264.35"),
                1708534800L
        );

        when(finnhubWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(FinnhubQuoteResponse.class)).thenReturn(Mono.just(response));
    }

    @Test
    void shouldFetchSaveAndPersistQuote() {
        StockQuote result = stockQuoteService.getQuote("AAPL");

        assertThat(result).isNotNull();
        assertThat(result.getSymbol()).isEqualTo("AAPL");
        assertThat(stockQuoteRepository.findAll()).hasSize(1);
    }

    @Test
    void shouldPersistMultipleQuotes() {
        stockQuoteService.getQuote("AAPL");
        stockQuoteService.getQuote("MSFT");

        assertThat(stockQuoteRepository.findAll()).hasSize(2);
    }
}