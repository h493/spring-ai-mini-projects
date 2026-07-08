package com.example.spring_ai_mini_project.config;

import com.example.spring_ai_mini_project.entity.Stock;
import com.example.spring_ai_mini_project.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds a handful of stocks on startup so the /chat/stock-bot tools
 * (getStockPrice / buyStock) have data to operate on. Skips seeding when
 * the table already has rows.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockDataLoader implements CommandLineRunner {

    private final StockRepository stockRepository;

    @Override
    public void run(String... args) {
        if (stockRepository.count() > 0) {
            log.info("Stocks already present, skipping seed.");
            return;
        }

        List<Stock> stocks = List.of(
                newStock(1L, "Apple", 210.50, 100),
                newStock(2L, "Google", 175.20, 100),
                newStock(3L, "Tesla", 250.00, 100),
                newStock(4L, "Amazon", 185.75, 100),
                newStock(5L, "Microsoft", 430.10, 100)
        );

        stockRepository.saveAll(stocks);
        log.info("Seeded {} stocks.", stocks.size());
    }

    private Stock newStock(Long id, String name, double price, int quantity) {
        Stock stock = new Stock();
        stock.setId(id);
        stock.setName(name);
        stock.setPrice(price);
        stock.setQuantity(quantity);
        return stock;
    }
}
