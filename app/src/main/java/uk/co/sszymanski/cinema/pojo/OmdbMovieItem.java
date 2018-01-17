package uk.co.sszymanski.cinema.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rex on 10/16/2016.
 */
public class OmdbMovieItem  implements Serializable{
    private String Runtime;
    private String Actors;
    private String Director;
    private String Writer;
    private String Awards;
    private String tomatoRating;
    private String imdbRating;
    private String tomatoMeter;
    private List<Ratings> Ratings = new ArrayList<>();


    public String getRuntime() {
        return Runtime;
    }

    public void setRuntime(String runtime) {
        this.Runtime = runtime;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getActors() {
        return Actors;
    }

    public void setActors(String actors) {
        Actors = actors;
    }

    public String getDirector() {
        return Director;
    }

    public void setDirector(String director) {
        Director = director;
    }

    public String getWriter() {
        return Writer;
    }

    public void setWriter(String writer) {
        Writer = writer;
    }

    public String getAwards() {
        return Awards;
    }

    public void setAwards(String awards) {
        Awards = awards;
    }

    public String getTomatoRating() {
        return tomatoRating;
    }

    public void setTomatoRating(String tomatoRating) {
        this.tomatoRating = tomatoRating;
    }

    public String getTomatoMeter() {
        return tomatoMeter;
    }

    public void setTomatoMeter(String tomatoMeter) {
        this.tomatoMeter = tomatoMeter;
    }

    public List<Ratings> getRatings() {
        return Ratings;
    }

    public void setRatings(List<Ratings> ratings) {
        this.Ratings = ratings;
    }
}
