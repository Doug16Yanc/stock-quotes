package tech.stockquotes.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tech.stockquotes.config.property.FinnhubProperties;
import tech.stockquotes.domain.StockQuote;
import tech.stockquotes.dto.FinnhubQuoteResponse;
import tech.stockquotes.mapper.StockQuoteMapper;
import tech.stockquotes.repository.StockQuoteRepository;
import tech.stockquotes.service.client.FinnhubClient;

@Service
@Slf4j
public class StockQuoteService {

    private final StockQuoteRepository stockQuoteRepository;
    private final StockQuoteMapper stockQuoteMapper;
    private final FinnhubProperties properties;
    private final FinnhubClient finnhubClient;
    private final CacheManager cacheManager;

    public StockQuoteService(
            StockQuoteRepository stockQuoteRepository,
            StockQuoteMapper stockQuoteMapper,
            FinnhubProperties properties,
            FinnhubClient finnhubClient,
            CacheManager cacheManager) {
        this.stockQuoteRepository = stockQuoteRepository;
        this.stockQuoteMapper = stockQuoteMapper;
        this.properties = properties;
        this.finnhubClient = finnhubClient;
        this.cacheManager = cacheManager;
    }

    @Cacheable(value = "quotes", key = "#symbol")
    public StockQuote getQuote(String symbol) {
        log.info("Cache MISS for symbol: {}", symbol);

        FinnhubQuoteResponse response = finnhubClient.fetchQuote(symbol);

        StockQuote quote = stockQuoteMapper.toEntity(symbol, response);

        return stockQuoteRepository.save(quote);
    }

    public StockQuote refreshQuote(String symbol) {
        log.info("Forced REFRESH for symbol: {}", symbol);

        FinnhubQuoteResponse response = finnhubClient.fetchQuote(symbol);
        StockQuote updated = stockQuoteMapper.toEntity(symbol, response);

        updated = stockQuoteRepository.save(updated);

        Cache cache = cacheManager.getCache("quotes");
        if (cache != null) {
            cache.put(symbol, updated);
        }

        return updated;
    }

    public void refreshQuotes() {
        properties.symbols().forEach(symbol -> {
            try {
                refreshQuote(symbol);
            } catch (Exception e) {
                log.error("Failed to refresh quote for {}: {}", symbol, e.getMessage());
            }
        });
    }
}