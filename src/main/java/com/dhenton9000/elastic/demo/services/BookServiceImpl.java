 

package com.dhenton9000.elastic.demo.services;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service 
public class BookServiceImpl implements BookService {
    
    @Autowired
    RestHighLevelClient client;
    
    
    public void stuff() {
        
        
    }

    @Override
    public RestHighLevelClient getClient() {
        return client;
    }

}
