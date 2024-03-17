package com.example.just.Config;

<<<<<<< HEAD
=======
import com.example.just.Repository.HashTagESRepository;
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
import com.example.just.Repository.PostContentESRespository;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;

@Configuration
<<<<<<< HEAD
@EnableReactiveElasticsearchRepositories(basePackageClasses = {PostContentESRespository.class})
=======
@EnableReactiveElasticsearchRepositories(basePackageClasses = {PostContentESRespository.class, HashTagESRepository.class})
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {
    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("34.22.67.43:9200")
                .build();
        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchOperations(){
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }

    @Bean
    public ElasticsearchRestTemplate elasticsearchRestTemplate() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }
}
