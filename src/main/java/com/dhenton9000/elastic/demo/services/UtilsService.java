package com.dhenton9000.elastic.demo.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.ClassPathResource;

public class UtilsService {

    
    private static final Logger LOG
            = LoggerFactory.getLogger(UtilsService.class);

    public static String getStringResource(String fileClassPath) {
        String content = null;
        ClassPathResource classPathResource = new ClassPathResource(fileClassPath);
        try {
            InputStream inr = classPathResource.getInputStream();
            content = new BufferedReader(new InputStreamReader(inr))
                    .lines().collect(Collectors.joining("\n"));
        } catch (IOException err) {
            LOG.warn("IO problem " + err.getMessage());
        }
        return content;
    }

}
