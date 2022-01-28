package fr.lernejo.search.api;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public final class GameInfoListener {
    private final RestHighLevelClient elasticRestClient;

    public GameInfoListener(RestHighLevelClient restClient){
        elasticRestClient = restClient;
    }

    @RabbitListener(queues = AmqpConfiguration.GAME_INFO_QUEUE)
    public void onMessage(Message message){
        IndexRequest request = new IndexRequest(
            "games","object", message.getMessageProperties().getHeader("game_id"));
        request.source(new String(message.getBody(), StandardCharsets.UTF_8), XContentType.JSON);
        try {
            elasticRestClient.index(request, RequestOptions.DEFAULT);
        }catch (Exception e){
            System.out.println("Can't index game, error : "+e.getMessage());
        }
    }
}
