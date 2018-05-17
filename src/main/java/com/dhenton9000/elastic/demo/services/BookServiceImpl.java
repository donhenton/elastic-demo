package com.dhenton9000.elastic.demo.services;

import com.dhenton9000.elastic.demo.model.BookResults;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    RestHighLevelClient client;

    private static final Logger LOG
            = LoggerFactory.getLogger(BookServiceImpl.class);

    @Override
    public Map<String, Object> getById(String id) {
        GetRequest getRequest = new GetRequest(INDEX, TYPE, id);
        GetResponse getResponse = null;
        try {
            getResponse = client.get(getRequest);
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
        }
        Map<String, Object> sourceAsMap = new HashMap();
        if (getResponse != null) {
            sourceAsMap = getResponse.getSourceAsMap();
        }

        return sourceAsMap;
    }

    @Override
    public List<Map<String, Object>> searchForText(String text) {
        List<Map<String, Object>> sourceList = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        QueryBuilder qBuilder = QueryBuilders.fuzzyQuery(TEXT_FIELDNAME,
                text);
        //.boost(1.0f).prefixLength(0)
        // .fuzziness(Fuzziness.ONE)
        //transpositions(true);

        searchSourceBuilder.query(qBuilder);

        searchRequest.source(searchSourceBuilder);

        try {
            Header h = new BasicHeader("request", "alpha");
            SearchResponse res = this.client.search(searchRequest, h);
            Arrays.asList(res.getHits().getHits()).forEach((SearchHit hit) -> {
                sourceList.add(hit.getSourceAsMap());
            });

        } catch (Exception ex) {
            LOG.error("io exception for search " + ex.getMessage());
        }
        return sourceList;
    }

    @Override
    public BookResults searchForAuthor(String authorName) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("author", authorName));
        sourceBuilder.from(0);
        sourceBuilder.size(5);
        SearchRequest searchRequest = new SearchRequest(INDEX);
        searchRequest.source(sourceBuilder);
        BookResults results = new BookResults();
        results.setCount(300);
        Header h = new BasicHeader("request", "alpha");
        try {

            SearchResponse res = this.client.search(searchRequest, h);
            Arrays.asList(res.getHits().getHits()).forEach((SearchHit hit) -> {
                results.getResults().add(hit.getSourceAsMap());
            });

        } catch (Exception ex) {
            LOG.error("io exception for search " + ex.getMessage());
        }

        return results;

    }

}
