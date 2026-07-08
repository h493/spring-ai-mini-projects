package com.example.spring_ai_mini_project.tools;

import com.example.spring_ai_mini_project.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockTools {

    private final StockService stockService;

    @Tool(description = "Return the stock price")
    public Double getStockPrice(
            @ToolParam(description = "Stock Name") String stockName){
        return stockService.getStockPrice(stockName);
    }

    @Tool(description = "Bought X shares of Y")
    public String buyStock(
            @ToolParam(description = "Stock name") String stockName,
            @ToolParam(description = "quantity") int quantity){
        stockService.buyStock(stockName, quantity);
        return "Bought %d share of %s".formatted(quantity, stockName);
    }

}
