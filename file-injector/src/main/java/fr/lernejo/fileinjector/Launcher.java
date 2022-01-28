package fr.lernejo.fileinjector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@SpringBootApplication
public class Launcher {
    private static final String GAME_INFO_QUEUE = "game_info";

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("No argument paste to file integrator or No file exist");
            return;
        }
        File file = new File(args[0]);
        JsonNode gamesList;
        ObjectMapper objectMapper = new ObjectMapper();
        if(file .exists()){
            try {
                gamesList = objectMapper.readTree(file);
            }catch (Exception e){
                System.out.println("Error during deserialization : "+e.getMessage());
                return;
            }
        }else{
            System.out.println("File not exist");
            return;
        }

        try (AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class)) {
            RabbitTemplate publisher = springContext.getBean(RabbitTemplate.class);
            for(JsonNode game : gamesList){
                if(!game.isEmpty() && game.has("id")){
                    publisher.send(GAME_INFO_QUEUE,MessageBuilder.withBody(game.toPrettyString().getBytes())
                        .setHeader("content_type", "application/json")
                        .setHeader("game_id", game.get("id")).build());
                }
            }
            System.out.println("Hello after starting Spring");
        }
    }
}
