package com.example.spring_ai_mini_project.dto;

import org.springframework.ai.document.Document;

import java.util.Map;

public record Song(String description, String genre) {

    public Document toDocument() {
        return new Document(description, Map.of("genre", genre));
    }
}
