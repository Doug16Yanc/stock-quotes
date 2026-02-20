package tech.stockquotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.stockquotes.domain.StockQuote;

import java.util.UUID;

@Repository
public interface StockQuoteRepository extends JpaRepository<StockQuote, UUID> {
}
