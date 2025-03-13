package school.faang.user_service.config.eks;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.io.IOException;

@Slf4j
@Configuration
public class ElasticSearchConfig {

    @Value("${elastic-search.url}")
    private String url;

    @Value("${elastic-search.username}")
    private String username;

    @Value("${elastic-search.password}")
    private String password;

    @Bean
    public ElasticsearchClient elasticsearchClient() throws IOException {
        ElasticsearchTransport transport = new RestClientTransport(
                restClient(), new JacksonJsonpMapper());

        ElasticsearchClient esClient = new ElasticsearchClient(transport);

        Goal goal = Goal.builder().id(1L).title("example-goal").description("description").build();
        IndexResponse response = esClient.index(i -> i.index("goals").id(goal.getTitle()).document(goal));
        log.info("Indexed with version: {}", response.version());
        log.info("Reponse: {}", response.index());

        return esClient;
    }

    @Bean
    public RestClient restClient() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));

        return RestClient
                .builder(HttpHost.create(url))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider))
                .build();
    }
}
