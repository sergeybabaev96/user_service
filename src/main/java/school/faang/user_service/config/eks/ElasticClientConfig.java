package school.faang.user_service.config.eks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration;

@Configuration
public class ElasticClientConfig extends ReactiveElasticsearchConfiguration {

    @Value("${elastic-search.url}")
    private String url;

    @Value("${elastic-search.username}")
    private String username;

    @Value("${elastic-search.password}")
    private String password;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(url)
                .withBasicAuth(username, password)
                .build();
    }
}
