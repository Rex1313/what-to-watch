package uk.co.sszymanski.cinema.interfaces;

/**
 * Created by rex on 10/16/2016.
 */
public interface ApiCallbacks {
    void onRequestSuccess(String response);

    void onRequestFailed(Exception e);
}
