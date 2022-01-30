package fr.lernejo.search.api;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class RestHighLevelClientImpl implements RestHighLevelClientWrapper{
    private final RestHighLevelClient restHighLevelClient;

    public RestHighLevelClientImpl(RestHighLevelClient client){
        restHighLevelClient = client;
    }

    @Override
    public IndexResponse index(IndexRequest request, RequestOptions options) throws IOException {
        return restHighLevelClient.index(request, options);
    }

    @Override
    public SearchResponse search(SearchRequest request, RequestOptions options) throws IOException {
        return restHighLevelClient.search(request, options);
    }
}
