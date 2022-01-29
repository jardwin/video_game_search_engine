package fr.lernejo.fileinjector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.io.File;

@SpringBootApplication
public class Launcher {
    private static final String GAME_INFO_QUEUE = "game_info";
    private static final String GAME_ID_HEADER = "game_id";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        final File file;
        final JsonNode gamesList;
        try {
            file = new File(args[0]);
            gamesList = objectMapper.readTree(file);
        } catch (Exception e) {
            System.out.println("Can't get or read the file : " + e.getMessage());
            return;
        }

        try (AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class)) {
            final RabbitTemplate publisher = springContext.getBean(RabbitTemplate.class);
            for (JsonNode game : gamesList) {
                try {
                    Message message = MessageBuilder.withBody(game.toPrettyString().getBytes())
                        .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                        .setHeader(GAME_ID_HEADER, game.get("id")).build();
                    publisher.send(GAME_INFO_QUEUE, message);
                } catch (Exception e) {
                    System.out.println("Can't send to rabbit queue");
                }
            }
            System.out.println("Hello after starting Spring");
        }
    }
}
