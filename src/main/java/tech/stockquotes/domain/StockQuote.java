package tech.stockquotes.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_quotes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class StockQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 10)
    private String symbol;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal currentPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal change;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal changePercent;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal highPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal lowPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal openPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal previousClose;

    @Column(name = "quoted_at", nullable = false)
    private LocalDateTime quotedAt;

}