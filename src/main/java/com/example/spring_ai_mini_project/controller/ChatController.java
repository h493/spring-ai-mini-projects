package com.example.spring_ai_mini_project.controller;

import com.example.spring_ai_mini_project.dto.Poem;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @GetMapping("/poem")
    public Poem getPoem(@RequestParam String topic, @RequestParam String language){

        String systemPrompt = """ 
                you are a sarcastic poet.
                Give a poem on topic : {topic} and in language : {language}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(systemPrompt);
        String renderText = promptTemplate.render(Map.of("topic", topic,
                                                    "language", language));
        return chatClient
                .prompt()
                .user(renderText)
                .call()
                .entity(Poem.class);
    }

    @GetMapping("/match-vibe")
    public String vibePlayListMatcher(@RequestParam String feeling){

        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(feeling)
                        .topK(3)
                        .build()
        );

        String context = String.join("\n\n", documents.stream().map(Document::getText).toList());

        String template = """
                Answer the question using only the context below.
                If the answer is not in the context, say you don't know.

                Context:
                {context}

                Feeling:
                {feeling}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        String renderedPrompt = promptTemplate.render(Map.of(
                "context", context,
                "feeling", feeling));

        return chatClient.prompt()
                .user(renderedPrompt)
                .call()
                .content();
    }

    public void addSongs(List<String> songs){
        List<Document> documents = songs.stream()
                .map(Document::new)
                .toList();
        vectorStore.add(documents);
    }
}
