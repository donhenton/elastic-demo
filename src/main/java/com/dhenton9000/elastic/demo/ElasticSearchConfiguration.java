package com.dhenton9000.elastic.demo;

import java.io.IOException;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfiguration extends AbstractFactoryBean {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchConfiguration.class);

    // only needed for spring data
    // @Value("${spring.data.elasticsearch.cluster-nodes}")
    // private String clusterNodes;
    // @Value("${spring.data.elasticsearch.cluster-name}")
    // private String clusterName;
    private RestHighLevelClient restHighLevelClient;

    @Value("${es.host}")
    private String esHost;
    @Value("${es.port}")
    private int esPort;

    @Override
    public void destroy() {
        try {
            if (restHighLevelClient != null) {
                restHighLevelClient.close();
            }
        } catch (final IOException e) {
            LOG.error("Error closing ElasticSearch client: ", e);
        }
    }

    @Override
    public Class<RestHighLevelClient> getObjectType() {
        return RestHighLevelClient.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public RestHighLevelClient createInstance() {
        return buildClient();
    }

    private RestHighLevelClient buildClient() {
        try {
            restHighLevelClient = new RestHighLevelClient(createClient());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return restHighLevelClient;
    }

    private RestClientBuilder createClient() {
        return RestClient.builder(
                new HttpHost(esHost, esPort, "http"));
    }

}
