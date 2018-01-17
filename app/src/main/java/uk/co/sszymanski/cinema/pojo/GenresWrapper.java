package uk.co.sszymanski.cinema.pojo;

import java.util.List;

/**
 * Created by rex on 10/13/2016.
 */
public class GenresWrapper {
    private  List<GenresItem> genres;

    public List<GenresItem> getGenres() {
        return genres;
    }

    public void setGenres(List<GenresItem> genres) {
        this.genres = genres;
    }
}
