package com.example.spring_ai_mini_project.config;

import com.example.spring_ai_mini_project.dto.Song;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
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
        if (isAlreadySeeded()) {
            log.info("Songs already present in vector store, skipping seed.");
            return;
        }

        List<Document> documents = SONGS.stream()
                .map(Song::toDocument)
                .toList();
        vectorStore.add(documents);
        log.info("Seeded {} songs into the vector store.", documents.size());
    }

    private boolean isAlreadySeeded() {
        return !vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("song")
                        .topK(1)
                        .build()
        ).isEmpty();
    }
}
