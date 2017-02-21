package com.example.wins.cinema.interfaces;

import com.example.wins.cinema.models.MainItem;

/**
 * Created by wins on 10/16/2016.
 */
public interface ApiCallbacks {
    void onRequestSuccess(String response);

    void onRequestFailed(Exception e);
}
