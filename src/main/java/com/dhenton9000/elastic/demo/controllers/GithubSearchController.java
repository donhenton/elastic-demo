package com.dhenton9000.elastic.demo.controllers;

import com.dhenton9000.elastic.demo.model.GithubResultsPage;
import com.dhenton9000.elastic.demo.model.HistogramData;
import com.dhenton9000.elastic.demo.services.GithubSearchService;
import io.swagger.annotations.ApiOperation;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/github/search")
public class GithubSearchController {

    @Autowired
    private GithubSearchService githubService;

    private static final Logger LOG = LoggerFactory.getLogger(GithubSearchController.class);

    @RequestMapping(method = RequestMethod.GET, path = "/uniqueterms", produces = "application/json")
    @ApiOperation(value = "Get Unique Terms", notes = "language and topics")

    public Map<String, List<Map<String, String>>> getUniqueTopicsAndLanguages() {

        return githubService.getUniqueTopicsAndLanguages();

    }

    @RequestMapping(method = RequestMethod.GET, path = "/entries/topics", produces = "application/json")
    @ApiOperation(value = "Get Entries by Topics", notes = "send in string delimited list at least one topic must match the list")

    public GithubResultsPage getEntriesByTopics(
            @RequestParam List<String> topics,
            @RequestParam int pageOffset) {

        return githubService.getEntriesByTopics(topics, pageOffset);

    }

    @RequestMapping(method = RequestMethod.GET, path = "/entries/all/topics", produces = "application/json")
    @ApiOperation(value = "Get Entries by All Topics", notes = "send in string delimited list topics must match all entries")

    public GithubResultsPage getEntriesByAllTopics(
            @RequestParam List<String> topics,
            @RequestParam int pageOffset) {

        return githubService.getEntriesByAllTopics(topics, pageOffset);

    }
    //@RequestParam(name = "date", required = false) to allow a parameter to be optional
    //https://blog.codecentric.de/en/2017/08/parsing-of-localdate-query-parameters-in-spring-boot/

    @RequestMapping(method = RequestMethod.GET, path = "/entries/dates", produces = "application/json")
    @ApiOperation(value = "Get Entries by date range", notes = "date range dates in format yyyy-MM-dd")
    public GithubResultsPage getEntriesByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
            @RequestParam int pageOffset) {

        return githubService.getEntriesByDate(start, end, pageOffset);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/year/histogram", produces = "application/json")
    @ApiOperation(value = "Get project count for months in a year", notes = "year in form YYYY, returns the count of projects for each month in that year")

    public HistogramData getYearHistogram(
            @RequestParam String year) {

        return githubService.getYearHistogram(year);

    }

    @RequestMapping(method = RequestMethod.GET, path = "/field/histogram", produces = "application/json")
    @ApiOperation(value = "Get project count for field", notes = "field is either stars or forks")

    public HistogramData getDataHistogramForField(String field) {
        return githubService.getDataHistogramForField(field);
    }

}
