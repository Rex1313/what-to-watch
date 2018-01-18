package uk.co.sszymanski.cinema.data;

import android.net.Uri;
import android.os.AsyncTask;

import uk.co.sszymanski.cinema.interfaces.ApiCallbacks;
import uk.co.sszymanski.cinema.utils.ParamsPair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rex on 10/16/2016.
 */
public class ApiTask extends AsyncTask<Void, Void, String> {
    private String baseUrl;
    private String requestUrl;
    private ApiCallbacks callback;

    public void start(ApiCallbacks mCallback) {
        this.callback = mCallback;
        this.execute();

    }

    public ApiTask(Builder apiTaskBuilder) {
        this.baseUrl = apiTaskBuilder.url;
        this.requestUrl = buildUrlRequest(apiTaskBuilder);

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
    protected String doInBackground(Void... voids) {
        String response;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                if (this.isCancelled()) {
                    return null;
                }
            }
            response = builder.toString();
        } catch (Exception e) {

            e.printStackTrace();
            return "";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;

    }

    @Override
    protected void onPostExecute(String item) {
        super.onPostExecute(item);
        if (!this.isCancelled()) {
            if (item.equals("")) {
                callback.onRequestFailed(new Exception("Returned an empty String"));
            } else {
                callback.onRequestSuccess(item);
            }
        }

    }


    public static class Builder {
        private String apiKey;
        private boolean isApiKeyRequired = false;
        private String url;
        private List<ParamsPair> args = new ArrayList<>();
        private List<String> paths = new ArrayList<>();


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



        public ApiTask build() {
            return new ApiTask(this);
        }

    }
}
