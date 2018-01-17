package uk.co.sszymanski.cinema.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import uk.co.sszymanski.cinema.pojo.Watched;

import java.util.List;

/**
 * Created by rex on 10/29/2016.
 */

public class StaticHelper {
    public static float dpToPx(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }


    public static void addWatched(Context context, int id){
        Watched watched = new Watched(id);
        PreferencesUtils utils = new PreferencesUtils(context);
        List<Watched> watchedList = utils.getWatchedMovies();

        boolean isInTheList = false;
        for(Watched item: watchedList){
            if(item.getId()==id){
                isInTheList = true;
            }
        }

        if(!isInTheList){
            watchedList.add(watched);
            utils.setWatchedMovies(watchedList);
        }
    }

    public static void removeWatched(Context context, int id){
        PreferencesUtils utils = new PreferencesUtils(context);
        List<Watched> watchedList = utils.getWatchedMovies();
        int positionToRemove = -1;
        for(int i =0; i<watchedList.size(); i++){
            if(watchedList.get(i).getId()==id){
                positionToRemove = i;
            }
        }
        if(positionToRemove!=-1){
            watchedList.remove(positionToRemove);
        }
        utils.setWatchedMovies(watchedList);

    }

}
