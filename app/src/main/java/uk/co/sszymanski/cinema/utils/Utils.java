package uk.co.sszymanski.cinema.utils;

import android.content.res.Resources;
import android.support.annotation.IdRes;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import uk.co.sszymanski.cinema.pojo.Watched;

import java.util.List;

/**
 * Created by rex on 10/29/2016.
 */

public class Utils {
    public static float dpToPx(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static <T extends View> T findView(ViewGroup rootView, @IdRes int childId){
        return rootView.findViewById(childId);
    }

}
