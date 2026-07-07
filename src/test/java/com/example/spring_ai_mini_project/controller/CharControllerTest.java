package com.example.spring_ai_mini_project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CharControllerTest {

    @Autowired
    private ChatController controller;

    @Test
    public void testAddSongs(){
        List<String> songs = List.of(
                "Bohemian Rhapsody by Queen - a dramatic, operatic ballad about guilt, confession and defiance, swinging between quiet despair and explosive rock catharsis.",
                "Here Comes the Sun by The Beatles - a warm, hopeful acoustic tune about relief and renewal after a long, cold and lonely winter.",
                "Lose Yourself by Eminem - an intense, motivational hip-hop anthem about seizing your one shot and refusing to let opportunity slip away.",
                "Someone Like You by Adele - a heartbroken piano ballad about accepting a lost love and wishing an old flame well despite the pain.",
                "Don't Stop Believin' by Journey - an uplifting stadium rock anthem about small-town dreamers holding on to hope against the odds.",
                "Blinding Lights by The Weeknd - a pulsing synthwave track about restless late-night longing and driving through the city chasing someone.",
                "Smells Like Teen Spirit by Nirvana - a raw, angsty grunge outburst channeling youthful boredom, rebellion and disillusionment.",
                "What a Wonderful World by Louis Armstrong - a gentle, grateful jazz standard celebrating the simple beauty of nature and everyday life.",
                "Shake It Off by Taylor Swift - a bubbly, defiant pop song about brushing off haters and dancing through the negativity.",
                "Hurt by Johnny Cash - a stark, mournful reflection on regret, loss and mortality at the end of a long life."
        );
        controller.addSongs(songs);
    }
}
