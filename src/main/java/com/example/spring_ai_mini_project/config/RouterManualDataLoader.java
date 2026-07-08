package com.example.spring_ai_mini_project.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Seeds the router user-manual PDF into the vector store on startup so the
 * /chat/support agent can answer "how-to" questions via RAG. Chunks are tagged
 * with type=manual so retrieval can be scoped to just the manual.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RouterManualDataLoader implements CommandLineRunner {

    private final VectorStore vectorStore;

    @Value("classpath:router_user_manual.pdf")
    private Resource manualPdf;

    @Override
    public void run(String... args) {
        if (isAlreadySeeded()) {
            log.info("Router manual already present in vector store, skipping seed.");
            return;
        }

        PagePdfDocumentReader reader = new PagePdfDocumentReader(manualPdf);
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(200)
                .build();

        List<Document> documents = splitter.apply(reader.get()).stream()
                .map(chunk -> {
                    Map<String, Object> metadata = new HashMap<>(chunk.getMetadata());
                    metadata.put("type", "manual");
                    return new Document(chunk.getText(), metadata);
                })
                .toList();

        vectorStore.add(documents);
        log.info("Seeded {} router-manual chunks into the vector store.", documents.size());
    }

    private boolean isAlreadySeeded() {
        return !vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("manual")
                        .topK(1)
                        .filterExpression("type == 'manual'")
                        .build()
        ).isEmpty();
    }
}
