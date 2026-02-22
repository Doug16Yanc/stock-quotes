package tech.stockquotes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record FinnhubQuoteResponse(
        @JsonProperty("c") BigDecimal currentPrice,
        @JsonProperty("d") BigDecimal change,
        @JsonProperty("dp") BigDecimal changePercent,
        @JsonProperty("h") BigDecimal highPrice,
        @JsonProperty("l") BigDecimal lowPrice,
        @JsonProperty("o") BigDecimal openPrice,
        @JsonProperty("pc") BigDecimal previousClose,
        @JsonProperty("t") Long timestamp
) {
    public static FinnhubQuoteResponse empty() {
        return new FinnhubQuoteResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0L);
    }
}