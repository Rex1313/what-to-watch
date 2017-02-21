package com.example.wins.cinema.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wins on 10/13/2016.
 */
public class MainItem {
    @SerializedName("total_pages")
    private int totalPages;
    private List<MovieItem> results;


    public List<MovieItem> getResults() {
        return results;
    }

    public void setResults(List<MovieItem> results) {
        this.results = results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
