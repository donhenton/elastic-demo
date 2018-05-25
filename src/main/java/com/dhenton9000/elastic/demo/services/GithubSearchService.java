/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dhenton9000.elastic.demo.services;

import com.dhenton9000.elastic.demo.model.GithubResultsPage;
import com.dhenton9000.elastic.demo.model.HistogramData;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dhenton
 */
public interface GithubSearchService {
    
    public int RESULTS_COUNT = 25;
    public static final String INDEX = "github";

    public Map<String,List<Map<String, String>>> getUniqueTopicsAndLanguages();
    public GithubResultsPage getEntriesByTopics(List<String> topics,int pageOffset);
    public GithubResultsPage getEntriesByAllTopics(List<String> topics,int pageOffset);
    public GithubResultsPage getEntriesByDate(LocalDate start, LocalDate end, int pageOffset);
    public HistogramData  getYearHistogram(String year);
    public HistogramData getDataHistogramForField(String field);
    
}
