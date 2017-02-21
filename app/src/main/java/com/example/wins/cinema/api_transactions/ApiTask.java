package com.example.wins.cinema.api_transactions;

import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.wins.cinema.interfaces.ApiCallbacks;
import com.example.wins.cinema.models.MainItem;
import com.example.wins.cinema.utils.ParamsPair;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wins on 10/16/2016.
 */
public class ApiTask extends AsyncTask<Void, Void, String> {
    private String apiKey;
    private String baseUrl;
    private String requestUrl;
    private ApiCallbacks mCallback;
    private ProgressBar progressBar;
    private boolean hasProgressBar = false;

    public void start(ApiCallbacks mCallback) {
        this.mCallback = mCallback;
        this.execute();

    }

    public ApiTask(Builder apiTaskBuilder) {
        this.apiKey = apiTaskBuilder.apiKey;
        this.baseUrl = apiTaskBuilder.url;
        this.requestUrl = buildUrlRequest(apiTaskBuilder);
        this.hasProgressBar = apiTaskBuilder.hasProgress;
        this.progressBar = apiTaskBuilder.progressBar;

    }


    private String buildUrlRequest(Builder apiTaskBuilder) {
        Uri.Builder uri = Uri.parse(baseUrl).buildUpon();
        if (apiTaskBuilder.isApiKeyRequired) {
            uri.appendQueryParameter("api_key", apiTaskBuilder.apiKey);
        }
        for (String path : apiTaskBuilder.paths) {
            uri.appendPath(path);
        }
        for (ParamsPair pair : apiTaskBuilder.args) {
            uri.appendQueryParameter(pair.getKey(), pair.getValue());
        }
        return uri.build().toString();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (hasProgressBar) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        String response = "";
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = "";
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                if (this.isCancelled()) {
                    return null;
                }
            }
            response = builder.toString();
            connection.disconnect();


        } catch (Exception e) {

            e.printStackTrace();
            return "";
        }

        return response;

    }

    @Override
    protected void onPostExecute(String item) {
        super.onPostExecute(item);
        if(!this.isCancelled()){
            if (hasProgressBar) {
                progressBar.setVisibility(View.GONE);
            }
            if (item.equals("")) {
                mCallback.onRequestFailed(new Exception("Returned an empty String"));
            } else {
                mCallback.onRequestSuccess(item);
            }
        }

    }


    public static class Builder {
        private String apiKey;
        private boolean isApiKeyRequired = false;
        private String url;
        private List<ParamsPair> args = new ArrayList<>();
        private List<String> paths = new ArrayList<>();
        private ProgressBar progressBar;
        private boolean hasProgress = false;


        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            this.isApiKeyRequired = true;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder arg(String key, String value) {
            this.args.add(new ParamsPair(key, value));
            return this;
        }

        public Builder appendPath(String key) {
            paths.add(key);
            return this;
        }

        public Builder addProgressBar(ProgressBar progressBar) {
            this.hasProgress = true;
            this.progressBar = progressBar;
            return this;
        }

        public ApiTask build() {
            return new ApiTask(this);
        }

    }
}
