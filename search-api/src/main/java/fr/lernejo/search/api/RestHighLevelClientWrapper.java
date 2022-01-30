package fr.lernejo.search.api;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;

import java.io.IOException;

public interface RestHighLevelClientWrapper {
    IndexResponse index(IndexRequest request, RequestOptions options) throws Exception;
    SearchResponse search(SearchRequest request, RequestOptions options) throws IOException;
}
