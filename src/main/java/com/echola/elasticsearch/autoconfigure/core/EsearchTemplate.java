package com.echola.elasticsearch.autoconfigure.core;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuchunming
 * @date 2023-06-30
 */
public class EsearchTemplate {
    private static final Logger log = LogManager.getLogger();
    private final RestHighLevelClient esRestClient;
    private long searchRequestTimeOut;

    public EsearchTemplate(RestHighLevelClient esRestClient, long searchRequestTimeOut) {
        this.searchRequestTimeOut = searchRequestTimeOut;
        this.esRestClient = esRestClient;
    }

    /**
     * 创建索引
     *
     * @param createIndexRequest
     * @param options
     * @return
     */
    public CreateIndexResponse createIndices(CreateIndexRequest createIndexRequest, RequestOptions options) {
        try {
            log.debug(createIndexRequest.toString());
            CreateIndexResponse createIndexResponse = esRestClient.indices().create(createIndexRequest, options);
            return createIndexResponse;
        } catch (IOException e) {
            throw new EsearchErrorException(e.getMessage(), e);
        } catch (ElasticsearchException e) {
            throw new EsearchErrorException(e.status(), e.getMessage(), e);
        }
    }

    /**
     * 删除索引
     *
     * @param deleteIndexRequest
     * @param options
     * @return
     */
    public AcknowledgedResponse deleteIndices(DeleteIndexRequest deleteIndexRequest, RequestOptions options) {
        try {
            log.debug(deleteIndexRequest.toString());
            AcknowledgedResponse acknowledgedResponse = esRestClient.indices().delete(deleteIndexRequest, options);
            return acknowledgedResponse;
        } catch (IOException e) {
            throw new EsearchErrorException(e.getMessage(), e);
        } catch (ElasticsearchException e) {
            throw new EsearchErrorException(e.status(), e.getMessage(), e);
        }
    }

    /**
     * 插入数据
     *
     * @param index  索引
     * @param id     ID
     * @param source 数据
     * @return
     */
    public IndexResponse index(String index, Object id, Object source) {
        IndexRequest indexRequest = new IndexRequest(index).id(String.valueOf(id));
        indexRequest.source(JSONUtil.toJsonStr(source), XContentType.JSON);
        indexRequest.type("_doc");
        return index(indexRequest, RequestOptions.DEFAULT);
    }

    /**
     * 插入数据
     *
     * @param indexRequest
     * @param options
     * @return
     */
    public IndexResponse index(IndexRequest indexRequest, RequestOptions options) {
        try {
            log.debug(indexRequest.toString());
            IndexResponse indexResponse = esRestClient.index(indexRequest, options);
            return indexResponse;
        } catch (IOException e) {
            throw new EsearchErrorException(e.getMessage(), e);
        } catch (ElasticsearchException e) {
            throw new EsearchErrorException(e.status(), e.getMessage(), e);
        }
    }

    /**
     * 获取数据
     *
     * @param getRequest
     * @param options
     * @return
     */
    public GetResponse get(GetRequest getRequest, RequestOptions options) {
        try {
            log.debug(getRequest.toString());
            GetResponse getResponse = esRestClient.get(getRequest, options);
            return getResponse;
        } catch (IOException e) {
            throw new EsearchErrorException(e.getMessage(), e);
        } catch (ElasticsearchException e) {
            throw new EsearchErrorException(e.status(), e.getMessage(), e);
        }
    }

    /**
     * 更新数据
     *
     * @param updateRequest
     * @param options
     * @return
     */
    public UpdateResponse update(UpdateRequest updateRequest, RequestOptions options) {
        try {
            log.debug(updateRequest.toString());
            UpdateResponse updateResponse = esRestClient.update(updateRequest, options);
            return updateResponse;
        } catch (IOException e) {
            throw new EsearchErrorException(e.getMessage(), e);
        } catch (ElasticsearchException e) {
            throw new EsearchErrorException(e.status(), e.getMessage(), e);
        }
    }

    /**
     * 删除数据
     *
     * @param deleteRequest
     * @param options
     * @return
     */
    public DeleteResponse delete(DeleteRequest deleteRequest, RequestOptions options) {
        try {
            log.debug(deleteRequest.toString());
            DeleteResponse deleteResponse = esRestClient.delete(deleteRequest, options);
            return deleteResponse;
        } catch (IOException e) {
            throw new EsearchErrorException(e.getMessage(), e);
        } catch (ElasticsearchException e) {
            throw new EsearchErrorException(e.status(), e.getMessage(), e);
        }
    }

    /**
     * 搜索
     *
     * @param searchSourceBuilder 搜索条件
     * @param clazz               结果class
     * @param indices             索引
     * @return 数据列表
     */
    public <T> List<T> search(SearchSourceBuilder searchSourceBuilder, Class<T> clazz, String... indices) {
        Page<T> resultPage = PageHelper.getLocalPage();
        boolean isResultPage = resultPage != null;
        if (isResultPage) {
            PageHelper.clearPage();
            searchSourceBuilder.from((int) resultPage.getStartRow());
            searchSourceBuilder.size(resultPage.getPageSize());
            searchSourceBuilder.trackTotalHits(true);
        }
        SearchResponse searchResponse = search(searchSourceBuilder, indices);
        List<T> resultList = formatSearchResult(searchResponse, clazz);
        if (isResultPage && resultPage.isCount()) {
            resultPage.setTotal(searchResponse.getHits().getTotalHits().value);
            resultPage.addAll(resultList);
            return resultPage;
        }
        return resultList;
    }

    /**
     * 查询
     *
     * @param searchSourceBuilder 搜索条件
     * @param clazz               结果class
     * @param indices             索引
     * @return 数据列表
     */
    public <T> List<T> select(SearchSourceBuilder searchSourceBuilder, Class<T> clazz, String... indices) {
        Page<T> resultPage = PageHelper.getLocalPage();
        boolean isResultPage = resultPage != null;
        if (isResultPage) {
            PageHelper.clearPage();
            searchSourceBuilder.from((int) resultPage.getStartRow());
            searchSourceBuilder.size(resultPage.getPageSize());
        }
        if (isResultPage && resultPage.isCount()) {
            CountResponse countResponse = count(searchSourceBuilder, indices);
            resultPage.setTotal(countResponse.getCount());
        }
        SearchResponse searchResponse = search(searchSourceBuilder, indices);
        List<T> resultList = formatSearchResult(searchResponse, clazz);
        if (isResultPage && resultPage.isCount()) {
            resultPage.addAll(resultList);
            return resultPage;
        }
        return resultList;
    }

    /**
     * 搜索
     *
     * @param searchSourceBuilder 搜索条件
     * @param indices             索引
     * @return 搜索结果
     */
    public SearchResponse search(SearchSourceBuilder searchSourceBuilder, String... indices) {
        SearchRequest searchRequest = new SearchRequest(indices);
        searchSourceBuilder.timeout(TimeValue.timeValueMillis(searchRequestTimeOut));
        searchRequest.source(searchSourceBuilder);
        return search(searchRequest, RequestOptions.DEFAULT);
    }

    public SearchResponse search(SearchRequest searchRequest, RequestOptions options) {
        try {
            log.debug(searchRequest.source().toString());
            return esRestClient.search(searchRequest, options);
        } catch (IOException e) {
            throw new EsearchErrorException(e.getMessage(), e);
        } catch (ElasticsearchException e) {
            throw new EsearchErrorException(e.status(), e.getMessage(), e);
        }
    }

    /**
     * 统计数量
     *
     * @param searchSourceBuilder 搜索条件
     * @param indices             索引
     * @return 统计结果
     */
    public CountResponse count(SearchSourceBuilder searchSourceBuilder, String... indices) {
        CountRequest countRequest = new CountRequest(indices);
        searchSourceBuilder.timeout(TimeValue.timeValueMillis(searchRequestTimeOut));
        countRequest.query(searchSourceBuilder.query());
        return count(countRequest, RequestOptions.DEFAULT);
    }

    public CountResponse count(CountRequest countRequest, RequestOptions options) {
        try {
            log.debug(countRequest.query().toString());
            return esRestClient.count(countRequest, options);
        } catch (IOException e) {
            throw new EsearchErrorException(e.getMessage(), e);
        } catch (ElasticsearchException e) {
            throw new EsearchErrorException(e.status(), e.getMessage(), e);
        }
    }

    /**
     * 聚合搜索
     *
     * @param searchSourceBuilder 搜索
     * @param indices             索引
     * @return 聚合数据
     */
    public Aggregations aggregate(SearchSourceBuilder searchSourceBuilder, String... indices) {
        SearchResponse searchResponse = search(searchSourceBuilder, indices);
        return searchResponse.getAggregations();
    }

    /**
     * 格式化搜索结果
     *
     * @param searchResponse 搜索结果
     * @param clazz          返回对象
     * @param <T>
     * @return
     */
    public <T> List<T> formatSearchResult(SearchResponse searchResponse, Class<T> clazz) {
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        List<T> resultList = new ArrayList<T>();
        for (SearchHit searchHit : searchHits) {
            resultList.add(JSONUtil.toBean(searchHit.getSourceAsString(), clazz));
        }
        return resultList;
    }

    /**
     * 获取ES请求客户端
     *
     * @return ES请求客户端
     */
    public RestHighLevelClient getRestHighLevelClient() {
        return esRestClient;
    }
}
