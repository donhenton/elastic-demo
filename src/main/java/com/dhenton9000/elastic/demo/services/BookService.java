/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dhenton9000.elastic.demo.services;

import org.elasticsearch.client.RestHighLevelClient;

/**
 *
 * @author dhenton
 */
public interface BookService {
    
    RestHighLevelClient getClient();
}
