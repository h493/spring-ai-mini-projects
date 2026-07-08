package com.example.spring_ai_mini_project.controller;

import com.example.spring_ai_mini_project.dto.Poem;
import com.example.spring_ai_mini_project.dto.Song;
import com.example.spring_ai_mini_project.tools.RouterTools;
import com.example.spring_ai_mini_project.tools.StockTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ChatMemory chatMemory;
    private final StockTools stockTools;
    private final RouterTools routerTools;

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


    @GetMapping("/ask-ai")
    public String askAI(@RequestParam String prompt,
                        @RequestParam(defaultValue = "default") String conversationId){
        return chatClient.prompt()
                .user(prompt)
                .advisors(advisor -> advisor
                        .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build(),
                                new SafeGuardAdvisor(List.of("competitor")))
                        // required in Spring AI 2.0 — identifies which conversation's
                        // history to load/save; there is no implicit default anymore.
                        .param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }


    @PostMapping("/chat/stock-bot")
    public String askStockBot(@RequestBody String message,
                              @RequestParam(defaultValue = "default") String conversationId){
        return chatClient.prompt()
                .user(message)
                .tools(stockTools)
                .advisors(advisor -> advisor
                        .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                        // required in Spring AI 2.0 — no implicit default conversation id
                        .param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }


    // Homework #6 — Customer Support Agent: RAG (user manual) + tool calling
    // (rebootRouter) + chat memory, so a multi-turn support conversation works.
    @PostMapping("/chat/support")
    public String customerSupport(@RequestBody String message,
                                  @RequestParam(defaultValue = "default") String conversationId){
        return chatClient.prompt()
                .system("""
                        You are a customer support agent for the Home Router RX-500.
                        The current customer's router serial number is RX500-00123.
                        Answer how-to and troubleshooting questions using the provided
                        user-manual context. If the customer asks you to restart, reboot,
                        or power-cycle their router, call the rebootRouter tool with their
                        serial number.
                        """)
                .user(message)
                .tools(routerTools)
                .advisors(advisor -> advisor
                        .advisors(
                                // RAG over just the router manual
                                QuestionAnswerAdvisor.builder(vectorStore)
                                        .searchRequest(SearchRequest.builder()
                                                .topK(4)
                                                .filterExpression("type == 'manual'")
                                                .build())
                                        .build(),
                                // memory so the two-step flow shares context
                                MessageChatMemoryAdvisor.builder(chatMemory).build())
                        .param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

}
