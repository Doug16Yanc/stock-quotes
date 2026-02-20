package tech.stockquotes;

import org.springframework.boot.SpringApplication;

public class TestStockQuotesApplication {

    public static void main(String[] args) {
        SpringApplication.from(StockQuotesApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
