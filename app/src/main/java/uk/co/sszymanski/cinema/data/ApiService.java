package uk.co.sszymanski.cinema.data;

import uk.co.sszymanski.cinema.interfaces.ApiCallbacks;
import uk.co.sszymanski.cinema.pojo.MovieItem;
import uk.co.sszymanski.cinema.utils.StaticValues;

/**
 * Created by rex on 17/01/2018.
 */

public class ApiService {


    /**
     * Gets Extra information from OMDB api based on Movie Title and release year from TMDB Api
     * @param movieItem MovieItem object build from TMB request
     * @param callback ApiCallback interface, result will be delivered through onRequestSuccess method
     */
    public static void getExtraMovieInfo(MovieItem movieItem, ApiCallbacks callback) {
        new ApiTask.Builder()
                .url(StaticValues.OMDB_BASE_URL)
                .arg("apiKey", StaticValues.OMDB_API_KEY)
                .arg("t", movieItem.getTitle().replace("'", "").replace(":", ""))
                .arg("y", movieItem.getReleaseDate().substring(0, 4))
                .arg("tomatoes", "true")
                .build().start(callback);
    }

    /**
     * Get all genres available in TMDB Api
     * @param callback ApiCallback interface, result will be delivered through onRequestSuccess method
     */
    public static void getGenres(ApiCallbacks callback) {
        new ApiTask.Builder()
                .url(StaticValues.TMDB_GENRES_URL)
                .apiKey(StaticValues.TMDB_API_KEY)
                .build().start(callback);
    }

    /**
     * Get list of movies from TMDB Api based on selected category and page number
     * @param page page number of request
     * @param category category of movies
     * @param callback ApiCallback interface, result will be delivered through onRequestSuccess method
     */
    public static void getSearchMovieList(int page, int category, ApiCallbacks callback) {
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

    /**
     * Get list of movies based on search query i.e "Bat" will return all movies whose title includes "Bat" sequence
     * @param query query to search
     * @param callback ApiCallback interface, result will be delivered through onRequestSuccess method
     */
    public static void getSearchMovieList(String query, ApiCallbacks callback) {
        new ApiTask.Builder()
                .url(StaticValues.TMDB_API_URL)
                .apiKey(StaticValues.TMDB_API_KEY)
                .appendPath("search")
                .appendPath("movie")
                .arg("query", query)
                .build().start(callback);
    }

    /**
     * Get list of video media related to specific movie
     * @param movieId TMBD movie id
     * @param callback ApiCallback interface, result will be delivered through onRequestSuccess method
     */
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
