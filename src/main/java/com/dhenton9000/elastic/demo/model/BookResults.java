package com.dhenton9000.elastic.demo.model;

import static com.dhenton9000.elastic.demo.services.BookService.DEFAULT_PAGE_COUNT;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonPropertyOrder({ "totalCount", "totalPages","pageOffset","perPageCount","results" })
public class BookResults {

    private long totalCount;
    private final int perPageCount = DEFAULT_PAGE_COUNT;
    private int pageOffset = 0;
    private List<Map<String, Object>> results;

    public BookResults() {
        this.totalCount = 0;
        this.results = new ArrayList<Map<String, Object>>();
    }

    /**
     * @return the totalCount
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * @param count the totalCount to set
     */
    public void setTotalCount(long count) {
        this.totalCount = count;
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

    /**
     * @return the perPageCount
     */
    public int getPerPageCount() {
        return perPageCount;
    }

    public void setPageOffset(int pageOffset) {
        this.pageOffset = pageOffset;
    }

    /**
     * @return the pageOffset
     */
    public int getPageOffset() {
        return pageOffset;
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
        hash = 71 * hash + (int) (this.totalCount ^ (this.totalCount >>> 32));
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
        if (this.totalCount != other.totalCount) {
            return false;
        }
        if (!Objects.equals(this.results, other.results)) {
            return false;
        }
        return true;
    }

}
