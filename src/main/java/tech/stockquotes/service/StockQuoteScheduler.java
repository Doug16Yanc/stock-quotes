package tech.stockquotes.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StockQuoteScheduler {

    private final StockQuoteService stockQuoteService;

    public StockQuoteScheduler(StockQuoteService stockQuoteService) {
        this.stockQuoteService = stockQuoteService;
    }

    @Scheduled(fixedRateString = "${finnhub.refresh-rate-ms:60000}")
    public void refreshQuotes() {
        log.info("Scheduled refresh starting...");
        stockQuoteService.refreshQuotes();
    }
}