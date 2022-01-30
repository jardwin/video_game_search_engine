package fr.lernejo.fileinjector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeTypeUtils;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.util.ArrayList;
import java.util.List;

public final class RabbitMessageBuilder {
    private static final String GAME_ID_HEADER = "game_id";
    private final ObjectMapper objectMapper;

    public RabbitMessageBuilder() {
        objectMapper = new ObjectMapper();
    }

    public List<Message> BuildMessagesFromFile(File input) throws Exception {
        final JsonNode gamesList;
        final List<Message> results = new ArrayList<>();
        try {
            gamesList = objectMapper.readTree(input);
        } catch (IOException e) {
            throw new IllegalClassFormatException("The file can't be read. Error : " + e.getMessage());
        }
        
        for (JsonNode jsonGame : gamesList) {
            results.add(MessageBuilder.withBody(jsonGame.toString().getBytes())
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .setHeader(GAME_ID_HEADER, jsonGame.get("id")).build());
        }
        return results;
    }
}
