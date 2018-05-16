package com.dhenton9000.elastic.demo.controllers;

import com.dhenton9000.elastic.demo.services.BookService;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.logging.Level;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class TextSearchController {

    @Autowired
    private BookService bookService;

    private static final Logger LOG = LoggerFactory.getLogger(TextSearchController.class);

    @RequestMapping(method = RequestMethod.GET, path = "/sample/{id}", produces = "text/html")
    @ApiOperation(value = "Sample", notes = "sample")
    public String findOne(@PathVariable("id") Integer id) {
        LOG.info("" + bookService);
        if (bookService == null) {
            return "book service null";
        } else {
            BasicHeader h = new BasicHeader("bonzo","dog");
            Header[] harray = new Header[1];
            harray[0] = h;
            String t = "ping fail ";
            try {
                t =  (bookService.getClient().ping(harray) + "");
                
            } catch (IOException ex) {
                 LOG.error("ping fail "+ex.getMessage());
            }
            return t;
        }
         
    }

    
}
