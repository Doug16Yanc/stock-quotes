package tech.stockquotes.mapper;

import org.springframework.stereotype.Component;
import tech.stockquotes.domain.StockQuote;
import tech.stockquotes.dto.FinnhubQuoteResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class StockQuoteMapper {

    public StockQuote toEntity(String symbol, FinnhubQuoteResponse response) {
        LocalDateTime quotedAt = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(response.timestamp()),
                ZoneId.of("America/New_York")
        );

        return new StockQuote(
                null,
                symbol,
                response.currentPrice(),
                response.change(),
                response.changePercent(),
                response.highPrice(),
                response.lowPrice(),
                response.openPrice(),
                response.previousClose(),
                quotedAt
        );
    }
}
