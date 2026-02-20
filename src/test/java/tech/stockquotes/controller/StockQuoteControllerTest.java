package tech.stockquotes.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tech.stockquotes.domain.StockQuote;
import tech.stockquotes.service.StockQuoteService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class StockQuoteControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

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
        registry.add("finnhub.symbols", () -> "AAPL,MSFT,JPM");
        registry.add("finnhub.base-url", () -> "http://localhost");
        registry.add("finnhub.refresh-rate-ms", () -> "60000");
    }
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockQuoteService stockQuoteService;

    @Test
    void shouldReturnStockQuoteBySymbol() throws Exception {
        StockQuote quote = new StockQuote(
                UUID.randomUUID(),
                "AAPL",
                new BigDecimal("260.58"),
                new BigDecimal("-3.77"),
                new BigDecimal("-1.4261"),
                new BigDecimal("264.48"),
                new BigDecimal("260.05"),
                new BigDecimal("262.60"),
                new BigDecimal("264.35"),
                LocalDateTime.now()
        );

        when(stockQuoteService.getQuote("AAPL")).thenReturn(quote);

        mockMvc.perform(get("/api/v1/stock-quotes/get-by-symbol/AAPL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.currentPrice").value(260.58));
    }

    @Test
    void shouldReturnOkForAnySymbol() throws Exception {
        when(stockQuoteService.getQuote("TSLA")).thenReturn(new StockQuote());

        mockMvc.perform(get("/api/v1/stock-quotes/get-by-symbol/TSLA"))
                .andExpect(status().isOk());
    }
}