package com.echola.elasticsearch.autoconfigure;

import com.echola.elasticsearch.autoconfigure.core.EsearchProperties;
import com.echola.elasticsearch.autoconfigure.core.EsearchTemplate;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Esearch configuration
 *
 * @Author: wanglei
 * @Date: 2020/12/16
 */
@Configuration
@ConditionalOnClass(RestHighLevelClient.class)
@EnableConfigurationProperties(EsearchProperties.class)
public class EsearchAutoConfiguration {
    @Bean
    RestClientBuilderCustomizer customizerRestClientBuilder(EsearchProperties esearchProperties) {
        return new EsearchAutoConfiguration.CustomizerRestClientBuilder(esearchProperties);
    }

    @Bean
    public EsearchTemplate esearchTemplate(RestHighLevelClient restHighLevelClient, EsearchProperties esearchProperties) {
        if (restHighLevelClient == null) {
            throw new NullPointerException("RestHighLevelClient init Error");
        }
        EsearchTemplate esearchTemplate = new EsearchTemplate(restHighLevelClient, esearchProperties.getConnect().getSearchRequestTimeout());
        return esearchTemplate;
    }

    public class CustomizerRestClientBuilder implements RestClientBuilderCustomizer {
        private final EsearchProperties.HttpClientConnect connect;

        public CustomizerRestClientBuilder(EsearchProperties esearchProperties) {
            this.connect = esearchProperties.getConnect();
        }

        @Override
        public void customize(RestClientBuilder builder) {
        }

        @Override
        public void customize(HttpAsyncClientBuilder builder) {
            builder.setMaxConnTotal(connect.getMaxConnTotal());
            builder.setMaxConnPerRoute(connect.getMaxConnPerRoute());
        }

        @Override
        public void customize(RequestConfig.Builder builder) {
            builder.setConnectionRequestTimeout(connect.getConnectionRequestTimeout());
        }
    }
}
