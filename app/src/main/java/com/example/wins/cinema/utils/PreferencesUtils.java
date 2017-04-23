package com.example.wins.cinema.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.wins.cinema.models.Watched;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rex on 17/04/2017.
 */

public class PreferencesUtils {
    private static PreferencesUtils preferencesUtils;
    private  Context context;
    public static PreferencesUtils getInstance(Context context){
        if(preferencesUtils==null){
            preferencesUtils = new PreferencesUtils();
        }
        preferencesUtils.setContext(context);
        return preferencesUtils;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void setWatchedMovies(List<Watched> watchedMovies){
        SharedPreferences preferences = context.getSharedPreferences("watched_movies", Context.MODE_PRIVATE);
//        Type collectionType = new TypeToken<Collection<Integer>>(){}.getType();
//        Collection<Integer> ints2 = gson.fromJson(json, collectionTy
        Gson gson = new Gson();
        Type type = new TypeToken<List<Watched>>(){}.getType();
        String serializedList = gson.toJson(watchedMovies, type);
        preferences.edit().putString("watchedMoviesList", serializedList).commit();

    }

    public List<Watched> getWatchedMovies(){
        SharedPreferences preferences = context.getSharedPreferences("watched_movies", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Watched>>(){}.getType();
        // inc case List was never initialized, lets initialize empty list to avoid errors when deserializing
        if(preferences.getString("watchedMoviesList", "").equals("")){
            setWatchedMovies(new ArrayList<Watched>());
        }
        return gson.fromJson(preferences.getString("watchedMoviesList", ""), type);
    }

    public boolean isDisplayWatched(){
        SharedPreferences preferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        return preferences.getBoolean("displayWatched", true);

    }
    public void setDisplayWatched(boolean displayWatched){
        SharedPreferences preferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("displayWatched", displayWatched).commit();

    }
}
