package tech.stockquotes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.stockquotes.domain.StockQuote;
import tech.stockquotes.service.StockQuoteService;

@RestController
@RequestMapping("/api/v1/stock-quotes")
public class StockQuoteController {

    private final StockQuoteService stockQuoteService;

    public StockQuoteController(StockQuoteService stockQuoteService) {
        this.stockQuoteService = stockQuoteService;
    }

    @GetMapping("/get-by-symbol/{symbol}")
    public ResponseEntity<StockQuote> getStockQuote(@PathVariable String symbol) {
        return ResponseEntity.ok().body(stockQuoteService.getQuote(symbol));
    }
}
