package tech.stockquotes.service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tech.stockquotes.config.property.FinnhubProperties;
import tech.stockquotes.dto.FinnhubQuoteResponse;

import java.time.Duration;

@Service
@Slf4j
public class FinnhubClientImpl implements  FinnhubClient {

    private final WebClient finnhubWebClient;
    private final FinnhubProperties properties;

    public FinnhubClientImpl(WebClient finnhubWebClient, FinnhubProperties properties) {
        this.finnhubWebClient = finnhubWebClient;
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        log.info("FinnhubClient class: {}", this.getClass().getName());
    }

    @CircuitBreaker(name = "financialApi", fallbackMethod = "fetchQuoteFallback")
    @Retry(name = "financialApi")
    public FinnhubQuoteResponse fetchQuote(String symbol) {
        log.info("Fetching quote from Finnhub for symbol: {}", symbol);
        return finnhubWebClient.get()
                .uri(uri -> uri.path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", properties.token())
                        .build())
                .retrieve()
                .bodyToMono(FinnhubQuoteResponse.class)
                .timeout(Duration.ofSeconds(3))
                .block();
    }

    private FinnhubQuoteResponse fetchQuoteFallback(String symbol, Exception e) {
        log.error("Fallback triggered for symbol: {} - reason: {}", symbol, e.getMessage());
        return FinnhubQuoteResponse.empty();
    }
}