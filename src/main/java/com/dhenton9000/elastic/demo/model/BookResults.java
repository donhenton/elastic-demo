package com.dhenton9000.elastic.demo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BookResults {

    private long count;
    private List<Map<String, Object>> results;

    public BookResults() {
        this.count = 0;
        this.results = new ArrayList<Map<String,Object>>();
    }

    public BookResults(long ct, List<Map<String, Object>> res) {
        this.count = ct;
        this.results = res;
    }


    /**
     * @return the count
     */
    public long getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(long count) {
        this.count = count;
    }

    /**
     * @return the results
     */
    public List<Map<String, Object>> getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(List<Map<String, Object>> results) {
        this.results = results;
    }
    
     @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (int) (this.count ^ (this.count >>> 32));
        hash = 71 * hash + Objects.hashCode(this.results);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BookResults other = (BookResults) obj;
        if (this.count != other.count) {
            return false;
        }
        if (!Objects.equals(this.results, other.results)) {
            return false;
        }
        return true;
    }
}
