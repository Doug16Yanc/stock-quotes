package tech.stockquotes.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "finnhub")
public record FinnhubProperties(
    String baseUrl,
    String token,
    long refreshRateMs,
    List<String> symbols
) {}
