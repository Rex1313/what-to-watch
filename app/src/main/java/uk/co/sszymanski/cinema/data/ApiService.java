package uk.co.sszymanski.cinema.data;

import uk.co.sszymanski.cinema.interfaces.ApiCallbacks;
import uk.co.sszymanski.cinema.pojo.MovieItem;
import uk.co.sszymanski.cinema.utils.StaticValues;

/**
 * Created by rex on 17/01/2018.
 */

public class ApiService {


    public static void getExtraMovieInfo(MovieItem movieItem, ApiCallbacks callback) {
        new ApiTask.Builder()
                .url(StaticValues.OMDB_BASE_URL)
                .arg("apiKey", StaticValues.OMDB_API_KEY)
                .arg("t", movieItem.getTitle().replace("'", "").replace(":", ""))
                .arg("y", movieItem.getReleaseDate().substring(0, 4))
                .arg("tomatoes", "true")
                .build().start(callback);
    }

    public static void getGenres(ApiCallbacks callback) {
        new ApiTask.Builder()
                .url(StaticValues.TMDB_GENRES_URL)
                .apiKey(StaticValues.TMDB_API_KEY)
                .build().start(callback);
    }

    public static void getMovieList(int page, int category, ApiCallbacks callback) {
        new ApiTask.Builder()
                .url(StaticValues.TMDB_API_URL)
                .apiKey(StaticValues.TMDB_API_KEY)
                .appendPath("discover")
                .appendPath("movie")
                .arg("sort_by", "popularity.desc")
                .arg("with_genres", String.valueOf(category))
                .arg("page", String.valueOf(page))
                .build().start(callback);
    }

    public static void getMovieList(String query, ApiCallbacks callback) {
        new ApiTask.Builder()
                .url(StaticValues.TMDB_API_URL)
                .apiKey(StaticValues.TMDB_API_KEY)
                .appendPath("search")
                .appendPath("movie")
                .arg("query", query)
                .build().start(callback);
    }

    public static void getMovieVideos(int movieId, ApiCallbacks callback){
        new ApiTask.Builder()
                .url(StaticValues.TMDB_API_URL)
                .apiKey(StaticValues.TMDB_API_KEY)
                .appendPath("movie")
                .appendPath(String.valueOf(movieId))
                .appendPath("videos")
                .build().start(callback);
    }
}
