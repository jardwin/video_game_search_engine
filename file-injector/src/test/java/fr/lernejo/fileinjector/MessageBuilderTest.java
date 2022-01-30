package fr.lernejo.fileinjector;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public final class MessageBuilderTest {

    @Test
    void main_send_message() {
        //<editor-fold desc="Arrange">
        final ObjectMapper mapper = new ObjectMapper();
        String firstBody = "";
        String secondBody = "";
        try {
            firstBody = mapper.readTree("""
                {
                        "id": 1,
                        "title": "Dauntless",
                        "thumbnail": "https:\\/\\/www.freetogame.com\\/g\\/1\\/thumbnail.jpg",
                        "short_description": "A free-to-play, co-op action RPG with gameplay similar to Monster Hunter.",
                        "game_url": "https:\\/\\/www.freetogame.com\\/open\\/dauntless",
                        "genre": "MMORPG",
                        "platform": "PC (Windows)",
                        "publisher": "Phoenix Labs",
                        "developer": "Phoenix Labs, Iron Galaxy",
                        "release_date": "2019-05-21",
                        "freetogame_profile_url": "https:\\/\\/www.freetogame.com\\/dauntless"
                    }""").toString();
            secondBody = mapper.readTree("""
                {
                        "id": 2,
                        "title": "World of Tanks",
                        "thumbnail": "https:\\/\\/www.freetogame.com\\/g\\/2\\/thumbnail.jpg",
                        "short_description": "If you like blowing up tanks, with a quick and intense game style you will love this game!",
                        "game_url": "https:\\/\\/www.freetogame.com\\/open\\/world-of-tanks",
                        "genre": "Shooter",
                        "platform": "PC (Windows)",
                        "publisher": "Wargaming",
                        "developer": "Wargaming",
                        "release_date": "2011-04-12",
                        "freetogame_profile_url": "https:\\/\\/www.freetogame.com\\/world-of-tanks"
                    }""").toString();
        } catch (IOException ignored) {
        }
        String file = getClass().getClassLoader().getResource("games_test.json").getFile();
        //</editor-fold>

        //<editor-fold desc="Act">
        RabbitMessageBuilder messageBuilder = new RabbitMessageBuilder();
        List<Message> messageResult;
        try {
            messageResult = messageBuilder.BuildMessagesFromFile(new File(file));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
            return;
        }
        assertEquals(messageResult.size(), 2);

        Message firstMessage = messageResult.get(0);
        Message secondMessage = messageResult.get(1);
        //</editor-fold>

        //<editor-fold desc="assert">
        assertEquals(firstMessage.getMessageProperties().getHeader("game_id").toString(), "1");
        assertEquals(new String(firstMessage.getBody()), new String(firstBody.getBytes()));
        assertEquals(secondMessage.getMessageProperties().getHeader("game_id").toString(), "2");
        assertEquals(new String(secondMessage.getBody()), new String(secondBody.getBytes()));
        //</editor-fold>
    }


    @Test
    void error_with_parsing_error() {
        try {
            new RabbitMessageBuilder().BuildMessagesFromFile(new File(getClass().getClassLoader().getResource("games_not_json.json").getFile()));
        } catch (Exception e) {
            assertEquals("The file can't be read. Error : Unrecognized token 'JE': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n" +
                " at [Source: (File); line: 1, column: 4]", e.getMessage());
        }
    }
}
