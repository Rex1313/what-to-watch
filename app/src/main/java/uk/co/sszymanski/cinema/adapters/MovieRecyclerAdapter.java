package uk.co.sszymanski.cinema.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.sszymanski.cinema.activities.DetailActivity;
import uk.co.sszymanski.cinema.activities.MainActivity;
import uk.co.sszymanski.cinema.R;
import uk.co.sszymanski.cinema.interfaces.MovieRecyclerInteractions;
import uk.co.sszymanski.cinema.pojo.MainItem;
import uk.co.sszymanski.cinema.pojo.MovieItem;
import uk.co.sszymanski.cinema.utils.PreferencesUtils;
import uk.co.sszymanski.cinema.utils.StaticValues;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rex on 10/13/2016.
 */
public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.MovieHolder> {
    private final String TAG = getClass().getSimpleName();
    private Context context;
    private List<MovieItem> movieItems = new ArrayList<>();
    private List<MovieItem> filteredList = new ArrayList<>();
    private MovieRecyclerInteractions callback;
    private int lastPosition = -1;
    private int pageLoaded = 0;
    private int totalPages = -1;
    public int lastRecyclerViewPosition = 0;
    private boolean isDisplayWatched;

    public MovieRecyclerAdapter(Context context, MainItem resultItem) {
        this.context = context;
        this.movieItems = resultItem.getResults();
        this.filteredList = filterOutWatchedMovies(movieItems);
        this.totalPages = resultItem.getTotalPages();
        this.isDisplayWatched = new PreferencesUtils(context).isDisplayWatched();

        try {
            this.callback = (MovieRecyclerInteractions) context;
        } catch (ClassCastException e) {
            Log.e(TAG, " Activity calling this adapter needs to implement MovieRecyclerInteractions interface");
        }
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(context).inflate(uk.co.sszymanski.cinema.R.layout.movie_card, parent, false);
            return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieHolder holder, final int position) {
            final MovieItem movieItem = isDisplayWatched?movieItems.get(position):filteredList.get(position);
            setTranslateAnimation(holder.itemView, position);
            holder.title.setText(movieItem.getTitle());
            if (!movieItem.getReleaseDate().equals("")) {
                holder.year.setText(movieItem.getReleaseDate().substring(0, 4));
            }
            holder.watched.setVisibility(movieItem.isWatched ? View.VISIBLE : View.GONE);

            holder.time.setText(movieItem.getVoteAverage());
            Picasso.with(context).load(StaticValues.POSTER_500_BASE_URL + movieItem.getPosterPath()).fit().centerCrop().into(holder.cover);

            holder.mainLayout.setOnClickListener(view -> {
                callback.onItemClicked(holder,movieItem, position);

            });
            holder.mainLayout.setOnLongClickListener(view -> {
                callback.onItemLongPressed(movieItem, position);
                if (movieItem.isWatched) {
                    callback.removeWatchedMovie(movieItem.getId());
                    movieItem.isWatched = false;
                } else {
                    callback.addWatchedMovie(movieItem.getId());
                    movieItem.isWatched = true;
                }
                notifyItemChanged(position);
                return true;
            });
            List<MovieItem> currentList = isDisplayWatched?movieItems:filteredList;
            if (position == currentList.size() - 1) {
                pageLoaded++;
                callback.loadNextPage(pageLoaded, totalPages);
            }

    }

    public List<MovieItem> getList() {
        return this.movieItems;
    }

    @Override
    public void onViewDetachedFromWindow(MovieHolder holder) {
        holder.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return isDisplayWatched?movieItems.size():filteredList.size();
    }

    private void setTranslateAnimation(View view, int position) {
        if (position > lastPosition) {
            TranslateAnimation anim = new TranslateAnimation(-300, 0, 0, 0);
            anim.setDuration(300);
            view.startAnimation(anim);
            lastPosition = position;
        }
    }

    private List<MovieItem> filterOutWatchedMovies(List<MovieItem> movieItems){
        return Stream.of(movieItems).filter(movie->!movie.isWatched).collect(Collectors.toList());
    }

    public void notifyChange(boolean isDisplayWatched) {
        filteredList = filterOutWatchedMovies(movieItems);
        this.isDisplayWatched = isDisplayWatched;
        notifyDataSetChanged();
    }


    public void refreshAdapter(MainItem mainItem, boolean replaceData, boolean isDisplayWatched) {
        this.isDisplayWatched = isDisplayWatched;
        if (replaceData) {
            totalPages = mainItem.getTotalPages();
            pageLoaded = 1;
            this.movieItems = mainItem.getResults();
            this.filteredList = filterOutWatchedMovies(mainItem.getResults());
        } else {
            this.movieItems.addAll(mainItem.getResults());
            this.filteredList.addAll(filterOutWatchedMovies(mainItem.getResults()));
        }
        notifyDataSetChanged();
    }

    public void clearAllData() {
        this.movieItems.clear();
        notifyDataSetChanged();
    }

    public class MovieHolder extends RecyclerView.ViewHolder {
        public TextView title, year, time, watched;
        public ImageView cover, backdrop;
        public CardView mainLayout;
        public View rootView;

        public MovieHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            title = itemView.findViewById(R.id.title);
            year = itemView.findViewById(R.id.releaseDate);
            time = itemView.findViewById(R.id.voteAverage);
            cover = itemView.findViewById(R.id.movie_cover);
            backdrop = itemView.findViewById(R.id.backdrop);
            watched = itemView.findViewById(R.id.watched_textview);
            mainLayout = (CardView) itemView;

        }

        protected void clearAnimation() {
            mainLayout.clearAnimation();
        }
    }
}
