package com.dhenton9000.elastic.demo.services;

import com.dhenton9000.elastic.demo.model.GithubEntry;
import com.dhenton9000.elastic.demo.model.GithubResultsPage;
import com.dhenton9000.elastic.demo.model.HistogramData;
import com.dhenton9000.elastic.demo.model.SuggestionList;
import static com.dhenton9000.elastic.demo.services.GithubSearchService.INDEX;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
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

        // LOG.debug("template " + restTemplate + " " + this.elasticSearchEndpoint);
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

                //  LOG.debug("putting "+agg.getName()+" "+dateData.getBuckets());
            });

            return returnedResults;

        } catch (IOException ex) {
            LOG.error("io exception for search " + ex.getMessage());
        }

        return returnedResults;

    }

    @Override
    public GithubResultsPage getEntriesByDate(LocalDate start, LocalDate end, int pageOffset) {

        List<GithubEntry> results = new ArrayList<>();
        GithubResultsPage page = setupPage(results, pageOffset);
        SearchSourceBuilder sourceBuilder = setupBuilder(pageOffset);
        LocalDateTime endT = end.atTime(0, 0, 30);
        LocalDateTime startT = start.atTime(0, 0, 30);
        // yyyy-MM-dd'T'HH:mm:ss
        RangeQueryBuilder query = QueryBuilders.rangeQuery("created").lte(endT).gte(startT);
        sourceBuilder.query(query);
        SearchRequest searchRequest = new SearchRequest(INDEX);
        // LOG.debug(sourceBuilder.toString());
        searchRequest.source(sourceBuilder);
        loadResults(searchRequest, page, results);
        return page;
    }

    private void loadResults(SearchRequest searchRequest, GithubResultsPage page, List<GithubEntry> results) {
        try {

            SearchResponse res = this.client.search(searchRequest);
            SearchHits searchHits = res.getHits();
            page.setTotalCount(searchHits.getTotalHits());
            Arrays.asList(searchHits.getHits()).forEach((SearchHit hit) -> {
                GithubEntry g = GithubEntry.createEntry(hit.getSourceAsMap());
                g.setId(hit.getId());
                results.add(g);
            });

        } catch (IOException ex) {
            LOG.error("io exception for search " + ex.getMessage());
        }
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
        listing = listing.substring(0, listing.length() - 1);
        listing = listing + "]";

        requestJSON = String.format(requestJSON, RESULTS_COUNT * pageOffset, RESULTS_COUNT, listing);
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
        loadResults(searchRequest, page, results);

        return page;
    }

    @Override
    public HistogramData getDataHistogramForField(String field) {
        HistogramData dataGram = new HistogramData();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(0); // don't return any samples
        List<Map<String, Object>> bucketData = new ArrayList<>();
        dataGram.setBucketData(bucketData);
        String aggName = field + "_histogram";
        List<String> allowedFields = Arrays.asList("stars", "forks");
        if (!allowedFields.contains(field)) {
            throw new RuntimeException("field '" + field
                    + "' is not allowed, only " + allowedFields.toString()
                    + " are allowed");
        }

        Double interval = 200.0d;
        Long minDocCount = 25l;

        if (allowedFields.get(0).equals(field)) {
            interval = 400.0d;
            minDocCount = 50l;
        }

        MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();
        sourceBuilder.query(query);
        HistogramAggregationBuilder agg
                = AggregationBuilders.histogram(aggName)
                        .field(field)
                        .interval(interval)
                        .minDocCount(minDocCount);
        sourceBuilder.aggregation(agg);
        SearchRequest searchRequest = new SearchRequest(INDEX);
        searchRequest.source(sourceBuilder);
        try {

            SearchResponse res = this.client.search(searchRequest);
            dataGram.setTotalHits(res.getHits().totalHits);
            if (res.getAggregations() != null) {
                ParsedHistogram dateData = res.getAggregations().get(aggName);
                dateData.getBuckets().forEach(b -> {
                    Map<String, Object> d = new HashMap<>();

                    Double scaledCount
                            = roundDouble(Double.valueOf(b.getDocCount()) / 1000.0d);

                    Double scaledInterval = Double.valueOf(b.getKeyAsString()) / 1000.0d;
                    String intervalString = String.format("%3.2f", scaledInterval);

                    d.put("count", scaledCount);
                    d.put("interval", intervalString);
                    bucketData.add(d);
                });
            }

        } catch (IOException ex) {
            LOG.error("io exception for search " + ex.getMessage());
        }

        return dataGram;
    }

    private Double roundDouble(Double val) {
        return new BigDecimal(val.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public HistogramData getYearHistogram(String year) {
        HistogramData yearGram = new HistogramData();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(0); // don't return any samples
        List<Map<String, Object>> bucketData = new ArrayList<>();
        yearGram.setBucketData(bucketData);

        String startYearStr = year + "-01-01";
        // LocalDate d = LocalDate.parse(year, DateTimeFormatter.ISO_DATE)
        LocalDate s = LocalDate.parse(startYearStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime startT = s.atTime(0, 0, 1);
        LocalDateTime endT = startT.plusYears(1);

        RangeQueryBuilder query = QueryBuilders.rangeQuery("created").lte(endT).gte(startT);
        sourceBuilder.query(query);

        sourceBuilder.aggregation(AggregationBuilders
                .dateHistogram("projects_over_time")
                .field("created")
                .format("yyy-MM-dd")
                .dateHistogramInterval(DateHistogramInterval.MONTH)
        );

        SearchRequest searchRequest = new SearchRequest(INDEX);
        // LOG.debug("start "+startT.toString()+" end "+endT.toString());
        searchRequest.source(sourceBuilder);
        try {

            SearchResponse res = this.client.search(searchRequest);
            yearGram.setTotalHits(res.getHits().totalHits);
            if (res.getAggregations() != null) {
                ParsedDateHistogram dateData = res.getAggregations().get("projects_over_time");
                dateData.getBuckets().forEach(b -> {
                    Map<String, Object> d = new HashMap<>();
                    d.put("count", b.getDocCount());
                    d.put("interval", b.getKeyAsString());
                    bucketData.add(d);
                });
            }

        } catch (IOException ex) {
            LOG.error("io exception for search " + ex.getMessage());
        }

        return yearGram;
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

    @Override
    public GithubResultsPage getEntriesByLanguage(String language, int pageOffset) {
        List<GithubEntry> results = new ArrayList<>();
        GithubResultsPage page = setupPage(results, pageOffset);
        SearchSourceBuilder sourceBuilder = setupBuilder(pageOffset);
        MatchQueryBuilder query = QueryBuilders.matchQuery("language", language);
        sourceBuilder.query(query);
        SearchRequest searchRequest = new SearchRequest(INDEX);
        // LOG.debug(sourceBuilder.toString());
        searchRequest.source(sourceBuilder);
        loadResults(searchRequest, page, results);

        return page;
    }

    @Override
    public SuggestionList getSuggestionsOnDescription(String entryText) {
        SuggestionList sList = new SuggestionList();
        sList.setInputText(entryText);
        List<GithubEntry> results = new ArrayList<>();
        String url = this.elasticSearchEndpoint + "/github/_search";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestJSON = UtilsService.getStringResource("queries/suggest.txt");
        requestJSON = String.format(requestJSON, entryText);
        //   LOG.debug(requestJSON);
        HttpEntity<String> entity = new HttpEntity<>(requestJSON, headers);

        ResponseEntity<HashMap> response = this.restTemplate.exchange(url, HttpMethod.POST, entity, HashMap.class);
        HashMap topMap = response.getBody();
        Map sg = (Map) topMap.get("suggest");

        List<Map<String, Object>> gList = (List<Map<String, Object>>) sg.get("github-suggest");
        Map<String, Object> gS = gList.get(0);
        List<Map<String, Object>> suggestions = (List<Map<String, Object>>) gS.get("options");
        suggestions.forEach(sugg -> {
            String id = (String) sugg.get("_id");

            Map<String, Object> source = (Map<String, Object>) sugg.get("_source");
            source.put("id", id);
            results.add(GithubEntry.createEntry(source));

        });
        sList.setSuggestions(results);
        return sList;
    }

    @Override
    public GithubResultsPage searchDescription(String searchTerm, int pageOffset) {
        //https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-search.html
        List<GithubEntry> results = new ArrayList<>();
        GithubResultsPage page = setupPage(results, pageOffset);
        SearchSourceBuilder sourceBuilder = setupBuilder(pageOffset);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlighter
                = new HighlightBuilder.Field("description");
        highlighter.highlighterType("unified");
        highlighter.preTags("<span class='show-highlight'>");
        highlighter.postTags("</span>");
        
        highlightBuilder.field(highlighter);
        sourceBuilder.highlighter(highlightBuilder);

        RegexpQueryBuilder query = QueryBuilders.regexpQuery("description", searchTerm + ".*");

        sourceBuilder.query(query);
        SearchRequest searchRequest = new SearchRequest(INDEX);

      //  LOG.debug(sourceBuilder.toString());
        searchRequest.source(sourceBuilder);
        //do the search
        try {

            SearchResponse res = this.client.search(searchRequest);
            SearchHits searchHits = res.getHits();
            page.setTotalCount(searchHits.getTotalHits());
            Arrays.asList(searchHits.getHits()).forEach((SearchHit hit) -> {

                GithubEntry g = GithubEntry.createEntry(hit.getSourceAsMap());
                g.setId(hit.getId());
               // LOG.info(hit.getHighlightFields().toString());
                HighlightField hField = hit.getHighlightFields().get("description");
                if (hField != null) {
                    Text[] fragments = hField.getFragments();
                    String accumText = "";
                    for (Text t : fragments) {
                        accumText = accumText + " " + t.toString();

                    }
                  g.setHighlightText(accumText);
                }
                
                results.add(g);
            });

        } catch (IOException ex) {
            LOG.error("io exception for search " + ex.getMessage());
        }

        return page;

    }

}
