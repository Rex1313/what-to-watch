package uk.co.sszymanski.cinema;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import uk.co.sszymanski.cinema.adapters.MovieRecyclerAdapter;

import uk.co.sszymanski.cinema.data.ApiService;
import uk.co.sszymanski.cinema.interfaces.ApiCallbacks;
import uk.co.sszymanski.cinema.interfaces.MovieRecyclerInteractions;
import uk.co.sszymanski.cinema.pojo.MainItem;
import uk.co.sszymanski.cinema.pojo.MovieItem;
import uk.co.sszymanski.cinema.pojo.Watched;
import uk.co.sszymanski.cinema.utils.PreferencesUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements MovieRecyclerInteractions {
    private final String TAG = getClass().getSimpleName();
    private RecyclerView mMovieRecyclerView;
    private List<MovieItem> mCurrentList = new ArrayList<>();
    private MovieRecyclerAdapter mAdapter;
    private ProgressBar mProgressBar;
    private SearchView mSearchView;
    private Gson mGson = new Gson();
    private int selectedCategory;
    private int page = 1;
    private int totalPages = 0;
    private List<Watched> watched;
    private PreferencesUtils mPreferenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        selectedCategory = getIntent().getIntExtra("category", 0);
        mMovieRecyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        mAdapter = new MovieRecyclerAdapter(this, mCurrentList);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mMovieRecyclerView.setLayoutManager(linearLayoutManager);
        mMovieRecyclerView.setAdapter(mAdapter);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mPreferenceUtils = new PreferencesUtils(this);
        watched = mPreferenceUtils.getWatchedMovies();
        loadMovieList();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RecyclerView.LayoutManager manager = new GridLayoutManager(this, 2);
            mMovieRecyclerView.setLayoutManager(manager);
            mMovieRecyclerView.getAdapter().notifyDataSetChanged();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
            mMovieRecyclerView.setLayoutManager(manager);
            mMovieRecyclerView.getAdapter().notifyDataSetChanged();
        }


    }

    public List<Watched> getWatched() {
        return this.watched;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMovieRecyclerView != null && mPreferenceUtils != null) {
            this.watched = mPreferenceUtils.getWatchedMovies();
            MovieRecyclerAdapter adapter = (MovieRecyclerAdapter) mMovieRecyclerView.getAdapter();
            List<MovieItem> items = adapter.getList();
            adapter.refreshAdapter(setWatchedMovies(items));
        }
    }

    private void loadMovieList() {
        ApiService.getMovieList(page, selectedCategory,new ApiCallbacks() {
            @Override
            public void onRequestSuccess(String response) {
                MainItem item = mGson.fromJson(response, MainItem.class);
                totalPages = item.getTotalPages();
                mAdapter.appendAdapterData(setWatchedMovies(item.getResults()));
                mProgressBar.setVisibility(View.GONE);
                getSupportActionBar().setTitle(String.valueOf(page) + "/" + String.valueOf(totalPages));
            }

            @Override
            public void onRequestFailed(Exception e) {
                Log.e(TAG, e.toString());
            }
        });
    }

    private List<MovieItem> setWatchedMovies(List<MovieItem> movies) {
        for (MovieItem movie : movies) {
            movie.isWatched = false;
            for (Watched watchedMovie : watched) {
                if (movie.getId() == watchedMovie.getId()) {
                    movie.isWatched = true;
                }
            }
        }
        if (!mPreferenceUtils.isDisplayWatched()) {
            List<MovieItem> items = new ArrayList<>();
            for (MovieItem movieItem : movies) {
                if (!movieItem.isWatched) {
                    items.add(movieItem);
                }
            }
            return items;
        }
        return movies;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem showWatchedCheckbox = menu.getItem(1);
        boolean isDisplayWatched = mPreferenceUtils.isDisplayWatched();
        showWatchedCheckbox.setChecked(isDisplayWatched);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (!newText.isEmpty()) {
                  ApiService.getMovieList(newText, new ApiCallbacks() {
                        @Override
                        public void onRequestSuccess(String response) {
                            MainItem item = mGson.fromJson(response, MainItem.class);
                            mAdapter.refreshAdapter(setWatchedMovies(item.getResults()));
                        }

                        @Override
                        public void onRequestFailed(Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    });
                } else {
                    mAdapter.refreshAdapter(new ArrayList<MovieItem>());
                    loadMovieList();
                }
                return true;

            }

        });
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_display_watched:
                if (item.isChecked()) {
                    item.setChecked(false);
                    mPreferenceUtils.setDisplayWatched(false);
                    mAdapter.notifyDataSetChanged();

                } else {
                    item.setChecked(true);
                    mPreferenceUtils.setDisplayWatched(true);
                    mAdapter.notifyDataSetChanged();

                }
                this.watched = mPreferenceUtils.getWatchedMovies();
                mAdapter.refreshAdapter(new ArrayList<MovieItem>());
                loadMovieList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadNextPage() {
        if (page < totalPages) {
            page++;
            loadMovieList();
        }
    }
}

