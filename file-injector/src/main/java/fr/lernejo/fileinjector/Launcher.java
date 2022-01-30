package fr.lernejo.fileinjector;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;
import java.util.List;

@SpringBootApplication
public class Launcher {
    private static final String GAME_INFO_QUEUE = "game_info";

    public static void main(String[] args) throws Exception {
        final RabbitMessageBuilder messageBuilder = new RabbitMessageBuilder();
        final List<Message> messageToBeSend;
        try {
            messageToBeSend = messageBuilder.BuildMessagesFromFile(new File(args[0]));
        } catch (Exception e) {
            System.err.println("Error during reading the file. Error : "+e.getMessage());
            return;
        }
        try (AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class)) {
            final RabbitTemplate publisher = springContext.getBean(RabbitTemplate.class);
            for (Message message : messageToBeSend) {
                try {
                    publisher.send(GAME_INFO_QUEUE, message);
                } catch (Exception e) {
                    System.err.println("Can't send to rabbit queue : " + GAME_INFO_QUEUE + ". Error : " + e.getMessage());
                }
            }
        }
    }
}
