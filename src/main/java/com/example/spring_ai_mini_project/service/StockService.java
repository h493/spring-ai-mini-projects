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
        return stockRepository.findByName(stockName).get().getPrice();
    }

    public void buyStock(String stockName, int quantity){

        Stock stock = stockRepository.findByName(stockName).get();
        int newQuantity = stock.getQuantity() - quantity;
        stock.setQuantity(newQuantity);
        stockRepository.save(stock);
    }
}
