package com.example.spring_ai_mini_project.service;

import com.example.spring_ai_mini_project.entity.Stock;
import com.example.spring_ai_mini_project.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public Double getStockPrice(String stockName){
        return findStock(stockName).getPrice();
    }

    public void buyStock(String stockName, int quantity){

        Stock stock = findStock(stockName);
        if (quantity > stock.getQuantity()) {
            throw new IllegalArgumentException(
                    "Only %d share(s) of %s available, cannot buy %d"
                            .formatted(stock.getQuantity(), stockName, quantity));
        }
        int newQuantity = stock.getQuantity() - quantity;
        stock.setQuantity(newQuantity);
        stockRepository.save(stock);
    }

    private Stock findStock(String stockName){
        return stockRepository.findByName(stockName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No stock found with name: " + stockName));
    }
}
