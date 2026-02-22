package tech.stockquotes.service.client;

import tech.stockquotes.dto.FinnhubQuoteResponse;

public interface FinnhubClient {
    FinnhubQuoteResponse fetchQuote(String symbol);
}