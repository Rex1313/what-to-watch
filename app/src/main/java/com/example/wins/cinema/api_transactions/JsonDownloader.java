package com.example.wins.cinema.api_transactions;

import android.content.Context;
import android.os.AsyncTask;

import com.example.wins.cinema.GlobalApplication;
import com.example.wins.cinema.interfaces.ApiCallbacks;
import com.example.wins.cinema.models.GenresItem;
import com.example.wins.cinema.models.GenresWrapper;
import com.example.wins.cinema.models.MainItem;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wins on 10/13/2016.
 */
public class JsonDownloader {
    private Context context;
    private ApiCallbacks mCallback;
    private GlobalApplication app;

    public JsonDownloader(Context context) {
        this.context = context;
        this.app = (GlobalApplication) context;
        this.mCallback = (ApiCallbacks) context;
    }

    public static class JsonDownloadTask extends AsyncTask<Void, Void,MainItem> {
        private final String API_KEY = "3f84b0894fce98a7ea89c598af027a55";
        private final String API_URL = "https://api.themoviedb.org/3/genre/28/movies?api_key="+API_KEY;
        private final String GENRES_URL = "https://api.themoviedb.org/3/genre/movie/list?api_key=3f84b0894fce98a7ea89c598af027a55&language=en-US";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MainItem doInBackground(Void... voids) {
            MainItem mainItem = new MainItem();
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line ="";
                StringBuilder builder = new StringBuilder();
                while ((line=reader.readLine())!=null)
                {
                    builder.append(line);
                }
                Gson gson = new Gson();
                mainItem = gson.fromJson(builder.toString(), MainItem.class);

                connection.disconnect();

                url = new URL(GENRES_URL);
                connection = (HttpURLConnection) url.openConnection();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                line ="";
                builder = new StringBuilder();
                while ((line=reader.readLine())!=null)
                {
                    builder.append(line);
                }
                GenresWrapper genresItem = gson.fromJson(builder.toString(), GenresWrapper.class);


                for(GenresItem item: genresItem.getGenres())
                {
//                    app.getGenres().put(item.getId(), item.getName());
                }



            } catch (Exception e) {
                e.printStackTrace();
            }

            return mainItem;
        }

        @Override
        protected void onPostExecute(MainItem item) {
            super.onPostExecute(item);
//            adapter.refreshAdapter(item.getResults());
        }
    }

}
