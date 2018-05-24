package com.dhenton9000.elastic.demo.controllers;


import com.dhenton9000.elastic.demo.model.GithubEntry;
import com.dhenton9000.elastic.demo.model.GithubResultsPage;
import com.dhenton9000.elastic.demo.services.GithubSearchService;
import io.swagger.annotations.ApiOperation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/github/search")
public class GithubSearchController implements GithubSearchService {

    @Autowired
    private GithubSearchService githubService;

    private static final Logger LOG = LoggerFactory.getLogger(GithubSearchController.class);
    
    
    @RequestMapping(method = RequestMethod.GET, path = "/uniqueterms", produces = "application/json")
    @ApiOperation(value = "Get Unique Terms", notes = "language and topics")
    @Override
    public Map<String,List<Map<String, String>>> getUniqueTopicsAndLanguages() {

       
       return githubService.getUniqueTopicsAndLanguages();
        

    }
    
    
    @RequestMapping(method = RequestMethod.GET, path = "/entries/topics", produces = "application/json")
    @ApiOperation(value = "Get Entries by Topics", notes = "send in string delimited list")
    @Override
    public GithubResultsPage  getEntriesByTopics(
            @RequestParam List<String> topics,
            @RequestParam int pageOffset ) {

        
       return githubService.getEntriesByTopics(topics,pageOffset);
        

    }

    
    

}
