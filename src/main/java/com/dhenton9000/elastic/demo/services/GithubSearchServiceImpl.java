package com.dhenton9000.elastic.demo.services;

import static com.dhenton9000.elastic.demo.services.GithubSearchService.INDEX;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
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
    public Map<String, List<Map<String, String>>> getUniqueTopicsAndLanguages() {

        Map<String, List<Map<String, String>>> returnedResults = new HashMap<>();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        QueryBuilder languageFilter
                = QueryBuilders.matchQuery("language.keyword", "");//.boost(0.0f).boost(0.0f);
        QueryBuilder topicsFilter
                = QueryBuilders.existsQuery("topics.keyword");//.boost(0.0f).boost(0.0f);

        QueryBuilder query = QueryBuilders.boolQuery()
                .mustNot(languageFilter)
                .must(topicsFilter); 

        sourceBuilder.size(0); // don't return any samples
        sourceBuilder.query(query);
        sourceBuilder.aggregation(AggregationBuilders.terms("unique_topics")
                .field("topics.keyword").size(20).minDocCount(0));
        sourceBuilder.aggregation(AggregationBuilders.terms("unique_lang")
                .field("language.keyword").size(20).minDocCount(0));

        
        // this dumps the json for the actual query
        // LOG.debug(sourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest(INDEX);

        searchRequest.source(sourceBuilder);
        Header h = createHeader();
        try {

            SearchResponse res = this.client.search(searchRequest, h);
            res.getAggregations().asList().forEach((Aggregation agg) -> {
                Terms termData = res.getAggregations().get(agg.getName());
                List<Map<String, String>> items = new ArrayList<>();
                termData.getBuckets().forEach(b -> {
                    Map<String, String> countPair = new HashMap<>();

                    String key = b.getKeyAsString();
                    //this has been filtered out (hopefully!)
                    if (key == null || key.isEmpty()) {
                        key = "UNDEF";
                    }

                    countPair.put("key", key);
                    countPair.put("docCount", b.getDocCount()+"");
                    items.add(countPair);

                });

                returnedResults.put(agg.getName(), items);

                //  LOG.debug("putting "+agg.getName()+" "+termData.getBuckets());
            });

            return returnedResults;

        } catch (IOException ex) {
            LOG.error("io exception for search " + ex.getMessage());
        }

        return returnedResults;

    }

    private Header createHeader() {
        Header h = new BasicHeader("request", "alpha");
        return h;
    }

   

}
