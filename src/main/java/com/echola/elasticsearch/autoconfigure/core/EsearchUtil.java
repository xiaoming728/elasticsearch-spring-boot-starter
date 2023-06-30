package com.echola.elasticsearch.autoconfigure.core;

import org.elasticsearch.action.search.SearchResponse;

/**
 * @author LiuChunMing
 * @date 2022-12-26
 */
public class EsearchUtil {

    /**
     * 根据es返回信息获取totol信息
     * @param searchResponse es响应
     * @return totol
     */
    public static long getTotal(SearchResponse searchResponse){
        return searchResponse.getHits().getTotalHits().value;
    }
}
