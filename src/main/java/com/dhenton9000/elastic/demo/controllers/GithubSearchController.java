package com.dhenton9000.elastic.demo.controllers;


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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/github/search")
public class GithubSearchController {

    @Autowired
    private GithubSearchService githubService;

    private static final Logger LOG = LoggerFactory.getLogger(GithubSearchController.class);
    
    
    @RequestMapping(method = RequestMethod.GET, path = "/uniqueterms", produces = "application/json")
    @ApiOperation(value = "Get Unique Terms", notes = "language and topics")
    public Map<String,List<Map<String, String>>> getAggs() {

       
       return githubService.getUniqueTopicsAndLanguages();
        

    }
    

}
