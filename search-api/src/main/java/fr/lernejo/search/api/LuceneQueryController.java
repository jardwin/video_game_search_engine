package fr.lernejo.search.api;

import org.apache.lucene.queryparser.classic.ParseException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@RestController
public class LuceneQueryController {

    private final RestHighLevelClient elasticClient;

    public LuceneQueryController(RestHighLevelClient client){
        elasticClient = client;
    }

    @GetMapping("/api/games")
    public ArrayList<Map<String, Object>> QueryAsLucene(final @RequestParam String query) throws IOException {
        final SearchRequest request = new SearchRequest("games");
        request.source().query(QueryBuilders.queryStringQuery(query));
        final SearchResponse response = elasticClient.search(request, RequestOptions.DEFAULT);
        final ArrayList<Map<String, Object>> content = new ArrayList<>();
        for (SearchHit item : response.getHits()) {
            content.add(item.getSourceAsMap());
        }
        return content;
    }
}

