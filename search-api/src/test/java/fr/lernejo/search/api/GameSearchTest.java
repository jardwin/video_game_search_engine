package fr.lernejo.search.api;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameSearchTest {

    @Test
    public void query_game_with_lucene_check_mapping_result() throws Exception {
        final LuceneQueryController controller = new LuceneQueryController(new RestHighLevelClientMock());
        final List<Map<String, Object>> response = controller.QueryAsLucene("developer:Cybertopia");
        assertEquals(2, response.size());
        assertEquals("Dauntless", response.get(0).get("title"));
        assertEquals("MMORPG", response.get(0).get("genre"));
        assertEquals("Phoenix Labs, Iron Galaxy", response.get(0).get("developer"));

        assertEquals("World of Tanks", response.get(1).get("title"));
        assertEquals("Shooter", response.get(1).get("genre"));
        assertEquals("Wargaming", response.get(1).get("developer"));
    }


    @Test
    public void query_game_with_lucene() throws Exception {
        final RestHighLevelClientMock restClientMock = new RestHighLevelClientMock();
        final LuceneQueryController controller = new LuceneQueryController(restClientMock);
        controller.QueryAsLucene("developer:Cybertopia");

        assertEquals("{\"query\":{\"query_string\":{\"query\":\"developer:Cybertopia\",\"fields\":[],\"type\":\"best_fields\",\"default_operator\":\"or\",\"max_determinized_states\":10000,\"enable_position_increments\":true,\"fuzziness\":\"AUTO\",\"fuzzy_prefix_length\":0,\"fuzzy_max_expansions\":50,\"phrase_slop\":0,\"escape\":false,\"auto_generate_synonyms_phrase_query\":true,\"fuzzy_transpositions\":true,\"boost\":1.0}}}" ,restClientMock.lastSearchRequest.source().toString());
    }
}
