package tech.stockquotes.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.stockquotes.domain.StockQuote;
import tech.stockquotes.service.StockQuoteService;

@RestController
@RequestMapping("/api/v1/stock-quotes")
@Slf4j
public class StockQuoteController {

    private final StockQuoteService stockQuoteService;

    public StockQuoteController(StockQuoteService stockQuoteService) {
        this.stockQuoteService = stockQuoteService;
    }

    @GetMapping("/get-by-symbol/{symbol}")
    public ResponseEntity<StockQuote> getStockQuote(@PathVariable String symbol) {
        long start = System.currentTimeMillis();
        StockQuote quote = stockQuoteService.getQuote(symbol);
        log.info("getQuote({}) took {}ms", symbol, System.currentTimeMillis() - start);
        return ResponseEntity.ok().body(quote);
    }
}
