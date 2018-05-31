 

package com.dhenton9000.elastic.demo.model;

import java.util.List;

 
public class SuggestionList {

    private String inputText;
    private List<GithubEntry> suggestions;

    /**
     * @return the inputText
     */
    public String getInputText() {
        return inputText;
    }

    /**
     * @param inputText the inputText to set
     */
    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    /**
     * @return the suggestions
     */
    public List<GithubEntry> getSuggestions() {
        return suggestions;
    }

    /**
     * @param suggestions the suggestions to set
     */
    public void setSuggestions(List<GithubEntry> suggestions) {
        this.suggestions = suggestions;
    }
    
}
