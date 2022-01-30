package fr.lernejo.search.api;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class GameInfoTest {
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setErr(originalErr);
    }

    @Test
    void send_message_to_elastic_with_success() {
        String json = "{\"id\":1,\"title\":\"toto\"}";
        IndexRequest request = new IndexRequest("test")
            .id("1")
            .source(json, XContentType.JSON);

        MessageProperties properties = new MessageProperties();
        properties.setHeader("game_id", "1");
        Message message = new Message(json.getBytes(StandardCharsets.UTF_8), properties);

        final RestHighLevelClientMock restHighLevelClient = new RestHighLevelClientMock();


        GameInfoListener listener = new GameInfoListener(restHighLevelClient);
        listener.onMessage(message);

        IndexRequest createdRequest = restHighLevelClient.lastIndexRequest;
        assertEquals(createdRequest.source(), request.source());
        assertEquals(createdRequest.id(), request.id());
    }

    @Test
    void send_message_to_elastic_with_error() {
        String json = "{\"id\":1,\"title\":\"toto\"}";

        MessageProperties properties = new MessageProperties();
        properties.setHeader("game_id", "1");
        Message message = new Message(json.getBytes(StandardCharsets.UTF_8), properties);

        final RestHighLevelClientMock restHighLevelClient = new RestHighLevelClientMock();
        restHighLevelClient.IsErrorOnIndex = true;

        GameInfoListener listener = new GameInfoListener(restHighLevelClient);
        try{
            listener.onMessage(message);
        }catch (Exception ignored){

        }

        assertEquals("Can't index game, Error : Error on indexing item to ElasticSearch\n", errContent.toString());
    }


}
