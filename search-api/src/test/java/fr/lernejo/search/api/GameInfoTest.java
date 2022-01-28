package fr.lernejo.search.api;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;

class GameInfoTest {

    @Test
    void SendMessageToElastic(){
        String json = "{\"id\":1,\"title\":\"toto\"}";
        MessageProperties properties = new MessageProperties();
        properties.setHeader("game_id", "1");
        GameInfoListener listener = new GameInfoListener(new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http"))));
        listener.onMessage(new Message(json.getBytes(StandardCharsets.UTF_8), properties));
    }
}
