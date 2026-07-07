package com.example.spring_ai_mini_project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Poem(String title,
                   @JsonProperty("poem_text") String poemText,
                   @JsonProperty("rhyme_scheme") String rhymeScheme){
}
