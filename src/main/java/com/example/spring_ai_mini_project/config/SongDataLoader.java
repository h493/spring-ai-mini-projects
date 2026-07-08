package com.example.spring_ai_mini_project.config;

import com.example.spring_ai_mini_project.dto.Song;
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

import java.util.List;

/**
 * Seeds the "vibe" playlist into the PGVector store on startup so that
 * GET /match-vibe has data to search. Each song carries a "genre" metadata
 * value (Rock / Pop) so results can be filtered by genre.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SongDataLoader implements CommandLineRunner {

    private final VectorStore vectorStore;

    @Value("classPath:Himanshu_Chhikara_Resume_SDE2.pdf")
    Resource pdfFile;

    private static final List<Song> SONGS = List.of(
            new Song("Fix You by Coldplay - a tender, building anthem about comforting someone who feels lost, defeated and ready to give up, promising to guide them home and help fix what hurts.", "Rock"),
            new Song("Bohemian Rhapsody by Queen - a dramatic operatic rock ballad swinging between guilt, confession, despair and explosive, defiant catharsis.", "Rock"),
            new Song("Smells Like Teen Spirit by Nirvana - a raw, angsty grunge outburst channeling youthful boredom, rebellion and disillusionment.", "Rock"),
            new Song("Don't Stop Believin' by Journey - an uplifting stadium rock anthem about small-town dreamers holding on to hope against the odds.", "Rock"),
            new Song("Here Comes the Sun by The Beatles - a warm, hopeful tune about relief and renewal after a long, cold and lonely winter.", "Rock"),
            new Song("With or Without You by U2 - a brooding, passionate rock ballad about the ache of a love you can neither hold on to nor let go.", "Rock"),
            new Song("Someone Like You by Adele - a heartbroken piano ballad about accepting a lost love and wishing an old flame well despite the pain.", "Pop"),
            new Song("Blinding Lights by The Weeknd - a pulsing synthwave pop track about restless late-night longing and chasing someone through the city.", "Pop"),
            new Song("Shake It Off by Taylor Swift - a bubbly, defiant pop song about brushing off haters and dancing through the negativity.", "Pop"),
            new Song("Happy by Pharrell Williams - a bright, feel-good pop song about unstoppable joy and feeling like a room without a roof.", "Pop")
    );

    @Override
    public void run(String... args) {
        ingestSongs();
        ingestPdf();
    }

    private void ingestSongs(){
        if (isAlreadySeeded("song")) {
            log.info("Songs already present in vector store, skipping seed.");
        }else {
            List<Document> documents = SONGS.stream()
                    .map(Song::toDocument)
                    .toList();
            vectorStore.add(documents);
            log.info("Seeded {} songs into the vector store.", documents.size());
        }
    }

    private boolean isAlreadySeeded(String query) {
        return !vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(1)
                        .build()
        ).isEmpty();
    }

    private void ingestPdf(){
        if (isAlreadySeeded("pdf")) {
            log.info("Pdf already present in vector store, skipping seed.");
        }else {
            PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfFile);
            List<Document> pages = reader.get();

            TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                    .withChunkSize(200)
                    .build();

            List<Document> documents = tokenTextSplitter.apply(pages);
            vectorStore.add(documents);
            log.info("Seeded {} pages into the vector store.", documents.size());
        }
    }
}
