package uk.co.sszymanski.cinema.interfaces;

import uk.co.sszymanski.cinema.adapters.MovieRecyclerAdapter;
import uk.co.sszymanski.cinema.pojo.MovieItem;

/**
 * Created by rex on 10/19/2016.
 */
public interface MovieRecyclerInteractions {
    void loadNextPage(int page, int totalPages);
    void addWatchedMovie(int movieId);
    void removeWatchedMovie(int movieId);
    void onItemClicked(MovieRecyclerAdapter.MovieHolder holder, MovieItem item, int position);
    void onItemLongPressed(MovieItem item, int position);
}
