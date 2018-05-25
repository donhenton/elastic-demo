 

package com.dhenton9000.elastic.demo.model;

import java.util.List;
import java.util.Map;

 
public class YearHistogram {

    private List<Map<String,Object>> bucketData;
    
    private long totalHits = 0;

    /**
     * @return the bucketData
     */
    public List<Map<String,Object>> getBucketData() {
        return bucketData;
    }

    /**
     * @param bucketData the bucketData to set
     */
    public void setBucketData(List<Map<String,Object>> bucketData) {
        this.bucketData = bucketData;
    }

    /**
     * @return the totalHits
     */
    public long getTotalHits() {
        return totalHits;
    }

    /**
     * @param totalHits the totalHits to set
     */
    public void setTotalHits(long totalHits) {
        this.totalHits = totalHits;
    }
    
    
    
    
}
