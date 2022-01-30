package fr.lernejo.search.api;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.elasticsearch.xcontent.DeprecationHandler;
import org.elasticsearch.xcontent.NamedXContentRegistry;
import org.elasticsearch.xcontent.ParseField;
import org.elasticsearch.xcontent.XContentParser;
import org.elasticsearch.xcontent.json.JsonXContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestHighLevelClientMock implements RestHighLevelClientWrapper{
    public IndexRequest lastIndexRequest;
    public SearchRequest lastSearchRequest;
    public boolean IsErrorOnIndex = false;

    private static List<NamedXContentRegistry.Entry> getDefaultXContentRegistryEntries() {
        List<NamedXContentRegistry.Entry> entries = new ArrayList<>();
        entries.add(new NamedXContentRegistry.Entry(Aggregation.class, new ParseField(StringTerms.NAME), (parser, content) -> ParsedStringTerms.fromXContent(parser, (String) content)));
        entries.add(new NamedXContentRegistry.Entry(Aggregation.class, new ParseField(TopHitsAggregationBuilder.NAME), (parser, content) -> ParsedTopHits.fromXContent(parser, (String) content)));
        entries.add(new NamedXContentRegistry.Entry(Suggest.Suggestion.class, new ParseField("term"), (parser, content) -> TermSuggestion.fromXContent(parser, (String) content)));
        entries.add(new NamedXContentRegistry.Entry(Suggest.Suggestion.class, new ParseField("phrase"), (parser, content) -> PhraseSuggestion.fromXContent(parser, (String) content)));
        return entries;
    }
    @Override
    public IndexResponse index(IndexRequest request, RequestOptions options) throws Exception {
        lastIndexRequest = request;
        if(IsErrorOnIndex)
            throw new Exception("Error on indexing item to ElasticSearch");
        return null;
    }

    @Override
    public SearchResponse search(SearchRequest request, RequestOptions options) throws IOException {
        lastSearchRequest = request;
        final NamedXContentRegistry registry = new NamedXContentRegistry(getDefaultXContentRegistryEntries());
        final String emptyResponse = """
            {
              "took" : 868,
              "timed_out" : false,
              "num_reduce_phases" : 2,
              "_shards" : {
                "total" : 722,
                "successful" : 722,
                "skipped" : 0,
                "failed" : 0
              },
              "hits" : {
                "total" : {
                  "value" : 2,
                  "relation" : "eq"
                },
                "max_score" : 1.0,
                "hits" : [{
                "_index" : ".kibana-event-log-7.12.0-000005",
                        "_type" : "_doc",
                        "_id" : "t-lCoHwBkaSlPV9W9Cou",
                        "_score" : 1.0,
                        "_source":{
                                  "id": 1,
                                  "title": "Dauntless",
                                  "genre": "MMORPG",
                                  "developer": "Phoenix Labs, Iron Galaxy"
                              }
                              },
                              {
                              "_index" : ".game",
                                      "_type" : "_doc",
                                      "_id" : "t-lCoHwBkaSlPV9W9Cop",
                                      "_score" : 1.0,
                                   "_source":{
                                  "id": 2,
                                  "title": "World of Tanks",
                                  "genre": "Shooter",
                                  "developer": "Wargaming"
                              }} ]
              }
            }""";
        final XContentParser parser = JsonXContent.jsonXContent.createParser(registry, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, emptyResponse);
        return SearchResponse.fromXContent(parser);
    }
}
