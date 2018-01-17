package uk.co.sszymanski.cinema.pojo;

import java.util.List;

/**
 * Created by rex on 10/16/2016.
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
