package com.dhenton9000.elastic.demo.controllers;

import com.dhenton9000.elastic.demo.services.BookService;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/search")
public class TextSearchController {

    @Autowired
    private BookService bookService;

    private static final Logger LOG = LoggerFactory.getLogger(TextSearchController.class);

    @RequestMapping(method = RequestMethod.GET, path = "/sample/{id}", produces = "application/json")
    @ApiOperation(value = "Get book by id", notes = "sample id AWNkBghhQVS53BGBIBHJ")
    public Map<String, Object> findBookById(@PathVariable("id") String id) {

        return bookService.getById(id);

    }

    @RequestMapping(method = RequestMethod.GET, path = "/search", produces = "application/json")
    @ApiOperation(value = "Search for text", notes = "fuzz freeform search")
    public List<Map<String, Object>> doQuery(@RequestParam(value = "text") String textToSearch) {
       
        return bookService.searchForText(textToSearch);
    }
}
