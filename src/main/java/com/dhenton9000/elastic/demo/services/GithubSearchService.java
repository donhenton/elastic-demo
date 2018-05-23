/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dhenton9000.elastic.demo.services;

import java.util.Map;

/**
 *
 * @author dhenton
 */
public interface GithubSearchService {
    public static final String INDEX = "github";

    public Map<String,Object> getUniqueTopicsAndLanguages();
}
