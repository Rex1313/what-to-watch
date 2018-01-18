package uk.co.sszymanski.cinema.utils;

import android.content.Context;
import android.content.SharedPreferences;

import uk.co.sszymanski.cinema.pojo.Watched;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rex on 17/04/2017.
 */

public class PreferencesUtils {
    private  Context mContext;

    public PreferencesUtils(Context context) {
        this.mContext = context;
    }

    public void setWatchedMovies(List<Watched> watchedMovies){
        SharedPreferences preferences = mContext.getSharedPreferences("watched_movies", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Watched>>(){}.getType();
        String serializedList = gson.toJson(watchedMovies, type);
        preferences.edit().putString("watchedMoviesList", serializedList).commit();


    }

    public List<Watched> getWatchedMovies(){
        SharedPreferences preferences = mContext.getSharedPreferences("watched_movies", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Watched>>(){}.getType();
        // in case List was never initialized, lets initialize empty list to avoid errors when deserializing
        if(preferences.getString("watchedMoviesList", "").equals("")){
            setWatchedMovies(new ArrayList<Watched>());
        }
        return gson.fromJson(preferences.getString("watchedMoviesList", ""), type);
    }

    public boolean isDisplayWatched(){
        SharedPreferences preferences = mContext.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        return preferences.getBoolean("displayWatched", true);

    }
    public void setDisplayWatched(boolean displayWatched){
        SharedPreferences preferences = mContext.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("displayWatched", displayWatched).commit();

    }
}
