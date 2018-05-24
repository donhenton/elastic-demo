package com.dhenton9000.elastic.demo.services;

import static com.dhenton9000.elastic.demo.services.GithubSearchService.INDEX;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GithubSearchServiceImpl implements GithubSearchService {

    @Autowired
    RestHighLevelClient client;

    public GithubSearchServiceImpl() {

    }

    private static final Logger LOG
            = LoggerFactory.getLogger(GithubSearchServiceImpl.class);

    @Override
    public Map<String, Object> getUniqueTopicsAndLanguages() {

        Map<String, Object> resObjs = new HashMap<String, Object>();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        QueryBuilder query = createKeyWordsExistQuery();
        sourceBuilder.size(0);
        sourceBuilder.query(query);
        sourceBuilder.aggregation(AggregationBuilders.terms("unique_topics").field("topics.keyword").size(20).minDocCount(0));
        sourceBuilder.aggregation(AggregationBuilders.terms("unique_lang").field("language.keyword").size(20).minDocCount(0));
        
        //this dumps the json for the actual query
        
        LOG.debug(sourceBuilder.toString());
        
        SearchRequest searchRequest = new SearchRequest(INDEX);

        searchRequest.source(sourceBuilder);
        Header h = createHeader();
        try {

            SearchResponse res = this.client.search(searchRequest, h);
            res.getAggregations().asList().forEach((Aggregation agg) -> {
                Terms termData = res.getAggregations().get(agg.getName());
                List<Map<String, Object>> items = new ArrayList<>();
                termData.getBuckets().forEach(b -> {
                    Map<String, Object> countPair = new HashMap<>();
                    
                    String key = b.getKeyAsString();
                    if (key == null || key.isEmpty()) {
                        key = "UNDEF";
                    }
                    
                    countPair.put("key",key);
                    countPair.put("docCount", b.getDocCount());
                    items.add(countPair);

                });

                resObjs.put(agg.getName(), items);

                //  LOG.debug("putting "+agg.getName()+" "+termData.getBuckets());
            });

            return resObjs;

        } catch (Exception ex) {
            LOG.error("io exception for search " + ex.getMessage());
        }

        return resObjs;

    }

    private Header createHeader() {
        Header h = new BasicHeader("request", "alpha");
        return h;
    }

    private BoolQueryBuilder createKeyWordsExistQuery() {
        QueryBuilder a1 =
               QueryBuilders.matchQuery("language.keyword","");//.boost(0.0f).boost(0.0f);
         QueryBuilder a2 = 
               QueryBuilders.existsQuery("topics.keyword");//.boost(0.0f).boost(0.0f);
         
        return QueryBuilders.boolQuery().mustNot(a1).must(a2);//.boost(0.0f) ;
        
    }

}
