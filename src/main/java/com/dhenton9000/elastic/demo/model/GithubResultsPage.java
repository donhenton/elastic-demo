package com.dhenton9000.elastic.demo.model;

import static com.dhenton9000.elastic.demo.services.GithubSearchService.RESULTS_COUNT;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonPropertyOrder({"totalCount", "totalPages", "pageOffset", "perPageCount", "results"})
public class GithubResultsPage {

    private long totalCount;
    private int perPageCount = RESULTS_COUNT;
    private int pageOffset = 0;
    private List<GithubEntry> results;

    /**
     * @return the totalCount
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * @param totalCount the totalCount to set
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the perPageCount
     */
    public int getPerPageCount() {
        return perPageCount;
    }

    /**
     * @param perPageCount the perPageCount to set
     */
    public void setPerPageCount(int perPageCount) {
        this.perPageCount = perPageCount;
    }

    /**
     * @return the pageOffset
     */
    public int getPageOffset() {
        return pageOffset;
    }

    /**
     * @param pageOffset the pageOffset to set
     */
    public void setPageOffset(int pageOffset) {
        this.pageOffset = pageOffset;
    }

    /**
     * @return the results
     */
    public List<GithubEntry> getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(List<GithubEntry> results) {
        this.results = results;
    }

    @JsonProperty
    public long totalPages() {
        long fl = Math.floorDiv(getTotalCount(), getPerPageCount());
        long extra = getTotalCount() % getPerPageCount();
        if (extra != 0) {
            fl++;
        }
        return fl;
    }

}
