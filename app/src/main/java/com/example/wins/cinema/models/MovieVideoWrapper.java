package com.example.wins.cinema.models;

import java.util.List;

/**
 * Created by wins on 10/16/2016.
 */
public class MovieVideoWrapper {
    private int id;
    private List<MovieVideoItem> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<MovieVideoItem> getResults() {
        return results;
    }

    public void setResults(List<MovieVideoItem> results) {
        this.results = results;
    }
}
