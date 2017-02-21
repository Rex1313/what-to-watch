package com.example.wins.cinema;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wins on 10/13/2016.
 */
public class GlobalApplication extends Application {
    private Map<Integer, String> genres = new HashMap<>();

    public Map<Integer, String> getGenres() {
        return genres;
    }

}
