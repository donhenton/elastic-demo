package com.dhenton9000.elastic.demo.controllers;

import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class TextSearchController {

    @RequestMapping(method = RequestMethod.GET, path = "/sample/{id}", produces = "application/json")
    @ApiOperation(value = "Sample", notes = "sample")
    public  Integer findOne(@PathVariable("id") Integer id) {
         return (id + 2000);
    }

}
