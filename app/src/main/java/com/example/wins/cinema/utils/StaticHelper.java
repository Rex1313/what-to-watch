package com.example.wins.cinema.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by rex on 10/29/2016.
 */

public class StaticHelper {
    public static float dpToPx(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

}
