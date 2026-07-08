package com.example.spring_ai_mini_project.controller;

import com.example.spring_ai_mini_project.dto.Poem;
import com.example.spring_ai_mini_project.dto.Song;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
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
    public Poem getPoem(@RequestParam String topic, @RequestParam("lang") String language){

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
    public String vibePlayListMatcher(@RequestParam String feeling,
                                      @RequestParam(required = false) String genre){

        SearchRequest.Builder request = SearchRequest.builder()
                .query(feeling)
                .topK(3);

        if (genre != null && !genre.isBlank()) {
            request.filterExpression("genre == '" + genre + "'");
        }

        List<Document> documents = vectorStore.similaritySearch(request.build());

        return documents.isEmpty() ? "No Match" : documents.get(0).getText();
    }

    @GetMapping("/resume-info")
    public String askResumeInfo(@RequestParam String question){
        return chatClient.prompt()
                .advisors(
                        //RAG advisor with custom tuning
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(4)
                                        .filterExpression("file_name == 'Himanshu_Chhikara_Resume_SDE2.pdf'" )
                                        .build())
                                .build()
                )
                .user(question)
                .call()
                .content();
    }

}
