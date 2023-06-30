package com.echola.elasticsearch.autoconfigure.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ES配置类
 *
 * @Author: wanglei
 * @Date: 2020/12/16
 */
@ConfigurationProperties(prefix = EsearchProperties.ECHOLA_PREFIX)
public class EsearchProperties {
    /**
     * echola prefix.
     */
    static final String ECHOLA_PREFIX = "spring.elasticsearch.rest";

    /**
     * Esearch
     */
    private HttpClientConnect connect;

    public HttpClientConnect getConnect() {
        return connect;
    }

    public void setConnect(HttpClientConnect connect) {
        this.connect = connect;
    }

    /**
     * esearch config
     */
    public static class HttpClientConnect {
        /**
         * 获取连接的超时时间
         */
        private int connectionRequestTimeout;
        /**
         * 获取搜索的超时时间
         */
        private long searchRequestTimeout = 10000L;
        /**
         * 最大连接数
         */
        private int maxConnTotal;
        /**
         * 最大路由连接数
         */
        private int maxConnPerRoute;

        public int getConnectionRequestTimeout() {
            return connectionRequestTimeout;
        }

        public void setConnectionRequestTimeout(int connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
        }

        public long getSearchRequestTimeout() {
            return searchRequestTimeout;
        }

        public void setSearchRequestTimeout(long searchRequestTimeout) {
            this.searchRequestTimeout = searchRequestTimeout;
        }

        public int getMaxConnTotal() {
            return maxConnTotal;
        }

        public void setMaxConnTotal(int maxConnTotal) {
            this.maxConnTotal = maxConnTotal;
        }

        public int getMaxConnPerRoute() {
            return maxConnPerRoute;
        }

        public void setMaxConnPerRoute(int maxConnPerRoute) {
            this.maxConnPerRoute = maxConnPerRoute;
        }
    }

}
