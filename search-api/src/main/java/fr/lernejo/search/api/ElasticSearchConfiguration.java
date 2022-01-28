package fr.lernejo.search.api;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfiguration {

    @Bean
    public RestHighLevelClient restHighLevelClient(
        final @Value("${elasticsearch.host:localhost}") String host,
        final @Value("${elasticsearch.port:9200}") int port,
        final @Value("${elasticsearch.username:elastic}") String userName,
        final @Value("${elasticsearch.password:admin}") String password){
        //Init the crédential to provied username and password
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName,password));

        //Create a RestClientBuild by passing server host and credential
        final RestClientBuilder builder = RestClient.builder(new HttpHost(host, port, "http"))
            .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

        return new RestHighLevelClient(builder);
    };
}
