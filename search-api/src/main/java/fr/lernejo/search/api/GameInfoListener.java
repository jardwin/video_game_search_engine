package fr.lernejo.search.api;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@SuppressWarnings("deprecation")
@Component
public class GameInfoListener {
    private final RestHighLevelClient elasticRestClient;
    private static final String GAME_INDEX = "games";
    private static final String GAME_ID_HEADER = "game_id";

    public GameInfoListener(RestHighLevelClient restClient){
        elasticRestClient = restClient;
    }

    @RabbitListener(queues = AmqpConfiguration.GAME_INFO_QUEUE)
    public void onMessage(final Message message){
        final IndexRequest request = new IndexRequest(GAME_INDEX)
        .id(message.getMessageProperties().getHeader(GAME_ID_HEADER))
        .source(new String(message.getBody(), StandardCharsets.UTF_8), XContentType.JSON);

        try {
            elasticRestClient.index(request, RequestOptions.DEFAULT);
        }catch (Exception e){
            System.out.println("Can't index game, error : "+e.getMessage());
        }
    }
}
