/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dhenton9000.elastic.demo.services;

import java.util.List;
import java.util.Map;
import org.elasticsearch.client.RestHighLevelClient;

/**
 *
 * @author dhenton
 */
public interface BookService {

    public static final String INDEX = "library";
    public final String TYPE = "book";
    public final String TEXT_FIELDNAME = "text";

    Map<String, Object> getById(String id);
    
    List<Map<String, Object>> searchForText(String text);

}
