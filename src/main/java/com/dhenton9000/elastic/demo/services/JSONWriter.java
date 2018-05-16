package com.dhenton9000.elastic.demo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.StringWriter;
import java.io.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 

@Service
public class JSONWriter {

    @Autowired
    ObjectMapper mapper;
    private static final Logger LOG = LoggerFactory.getLogger(JSONWriter.class);

    public String getJSON(Object o) {
        Writer w = new StringWriter();
       
        
        try {
            mapper.writeValue(w, o);
        } catch (IOException ex) {
            LOG.error("io problem with json "+ex.getMessage());
        }
         
        return w.toString();
    }

}
