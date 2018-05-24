package com.dhenton9000.elastic.demo.services;

import com.dhenton9000.elastic.demo.model.GithubEntry;
import com.dhenton9000.elastic.demo.model.GithubResultsPage;
import static com.dhenton9000.elastic.demo.services.GithubSearchService.INDEX;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GithubSearchServiceImpl implements GithubSearchService {

    @Autowired
    RestHighLevelClient client;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private RestTemplate restTemplate;

    @Value("http://${es.host}:${es.port}")
    private String elasticSearchEndpoint;

    public GithubSearchServiceImpl() {

    }

    private static final Logger LOG
            = LoggerFactory.getLogger(GithubSearchServiceImpl.class);

    @Override
    public Map<String, List<Map<String, String>>> getUniqueTopicsAndLanguages() {

        LOG.debug("template " + restTemplate + " " + this.elasticSearchEndpoint);
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
        try {

            SearchResponse res = this.client.search(searchRequest);
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
                    countPair.put("docCount", b.getDocCount() + "");
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

    @Override
    public GithubResultsPage getEntriesByAllTopics(List<String> topics, int pageOffset) {
        List<GithubEntry> results = new ArrayList<>();
        GithubResultsPage page = this.setupPage(results, pageOffset);
        String url = this.elasticSearchEndpoint + "/github/_search";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestJSON = UtilsService.getStringResource("queries/allTopics.txt");

        StringBuilder b = new StringBuilder();
        b.append("[");
        topics.forEach(s -> {
            b.append("\"");
            b.append(s);
            b.append("\"");
            b.append(",");
        });
        
        String listing = b.toString();
        listing = listing.substring(0,listing.length()-1);
        listing = listing + "]";
        
        requestJSON = String.format(requestJSON,RESULTS_COUNT * pageOffset,RESULTS_COUNT,listing);
     //   LOG.debug(requestJSON);
        HttpEntity<String> entity = new HttpEntity<>(requestJSON, headers);

        ResponseEntity<HashMap> response = this.restTemplate.exchange(url, HttpMethod.POST, entity, HashMap.class);

        Integer total = buildHitsFromResponse(response, results);
        page.setResults(results);
        page.setTotalCount(total);

        return page;
    }

    private Integer buildHitsFromResponse(ResponseEntity<HashMap> response, List<GithubEntry> results) {
        HashMap topMap = response.getBody();
        if (topMap == null) {
            return 0;
        }
        Map hitsMap = (Map) topMap.get("hits");
        Integer total = (Integer) hitsMap.get("total");
        List<Map<String, Object>> hitList = (List<Map<String, Object>>) hitsMap.get("hits");
        hitList.forEach(objMap -> {
            Map<String, Object> sourceData = (Map<String, Object>) objMap.get("_source");
            results.add(GithubEntry.createEntry(sourceData));
        });
        return total;
    }

    @Override
    public GithubResultsPage getEntriesByTopics(List<String> topics, int pageOffset) {
        List<GithubEntry> results = new ArrayList<>();
        GithubResultsPage page = setupPage(results, pageOffset);
        SearchSourceBuilder sourceBuilder = setupBuilder(pageOffset);
        TermsQueryBuilder query = QueryBuilders.termsQuery("topics", topics);
        sourceBuilder.query(query);
        SearchRequest searchRequest = new SearchRequest(INDEX);
       // LOG.debug(sourceBuilder.toString());
        searchRequest.source(sourceBuilder);
        try {

            SearchResponse res = this.client.search(searchRequest);
            SearchHits searchHits = res.getHits();
            page.setTotalCount(searchHits.getTotalHits());
            Arrays.asList(searchHits.getHits()).forEach((SearchHit hit) -> {
                GithubEntry g = GithubEntry.createEntry(hit.getSourceAsMap());
                results.add(g);
            });

        } catch (IOException ex) {
            LOG.error("io exception for search " + ex.getMessage());
        }

        return page;
    }

    private SearchSourceBuilder setupBuilder(int pageOffset) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(RESULTS_COUNT * pageOffset);
        sourceBuilder.size(RESULTS_COUNT);
        return sourceBuilder;
    }

    /**
     * setup a page to handle pagination
     *
     * @param results
     * @param pageOffset
     * @return
     */
    private GithubResultsPage setupPage(List<GithubEntry> results, int pageOffset) {

        GithubResultsPage page = new GithubResultsPage();
        page.setResults(results);
        page.setPerPageCount(RESULTS_COUNT);
        page.setPageOffset(pageOffset);
        return page;
    }
}
