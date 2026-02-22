package tech.stockquotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import tech.stockquotes.config.property.FinnhubProperties;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableConfigurationProperties(FinnhubProperties.class)
@EnableAspectJAutoProxy
public class StockQuotesApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockQuotesApplication.class, args);
    }

}
