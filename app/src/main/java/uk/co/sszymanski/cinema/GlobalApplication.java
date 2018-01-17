package uk.co.sszymanski.cinema;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rex on 10/13/2016.
 */
public class GlobalApplication extends Application {
    private Map<Integer, String> genres = new HashMap<>();

    public Map<Integer, String> getGenres() {
        return genres;
    }

}
