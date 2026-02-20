package tech.stockquotes.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tech.stockquotes.config.property.FinnhubProperties;
import tech.stockquotes.domain.StockQuote;
import tech.stockquotes.dto.FinnhubQuoteResponse;
import tech.stockquotes.mapper.StockQuoteMapper;
import tech.stockquotes.repository.StockQuoteRepository;

@Service
public class StockQuoteService {

    private final StockQuoteRepository stockQuoteRepository;
    private final StockQuoteMapper stockQuoteMapper;
    private final FinnhubProperties properties;
    private final WebClient finnhubWebClient;

    public StockQuoteService(StockQuoteRepository stockQuoteRepository, StockQuoteMapper stockQuoteMapper, FinnhubProperties properties, WebClient finnhubWebClient) {
        this.stockQuoteRepository = stockQuoteRepository;
        this.stockQuoteMapper = stockQuoteMapper;
        this.properties = properties;
        this.finnhubWebClient = finnhubWebClient;
    }

    public StockQuote getQuote(String symbol) {
        FinnhubQuoteResponse response = finnhubWebClient.get()
                .uri(uri -> uri.path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", properties.token())
                        .build()
                )
                .retrieve()
                .bodyToMono(FinnhubQuoteResponse.class)
                .block();

        StockQuote stockQuote = stockQuoteMapper.toEntity(symbol, response);

        return stockQuoteRepository.save(stockQuote);
    }

    @Scheduled(fixedRateString = "${finnhub.refresh-rate-ms:60000}")
    public void refreshQuotes() {
        properties.symbols().forEach(symbol -> {
                try {
                    getQuote(symbol);
                } catch (Exception e) {
                    /* circuit breaker will handle it. Does not propagate */
                }
        });
    }
}
