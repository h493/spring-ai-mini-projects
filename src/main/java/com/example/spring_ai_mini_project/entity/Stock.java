package com.example.spring_ai_mini_project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Stock {

    @Id
    private Long id;

    private Double price;
    private String name;
    private int quantity;
}
