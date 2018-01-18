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
import uk.co.sszymanski.cinema.data.DatabaseHelper;
import uk.co.sszymanski.cinema.interfaces.ApiCallbacks;
import uk.co.sszymanski.cinema.interfaces.MovieRecyclerInteractions;
import uk.co.sszymanski.cinema.pojo.MainItem;
import uk.co.sszymanski.cinema.pojo.MovieItem;
import uk.co.sszymanski.cinema.pojo.Watched;
import uk.co.sszymanski.cinema.utils.PreferencesUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements MovieRecyclerInteractions, SearchView.OnQueryTextListener {
    private final String TAG = getClass().getSimpleName();
    private RecyclerView movieRecyclerView;
    private ProgressBar progressBar;
    private DatabaseHelper dbHelper;
    private List<Watched> watched;
    private int categoryId;
    private PreferencesUtils preferenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        categoryId = getIntent().getIntExtra("categoryId", 0);
        String categoryString = getIntent().getStringExtra("categoryName");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoryString);
        }

        init();
        ApiService.getMovieList(1, categoryId, new ApiCallbacks() {
            @Override
            public void onRequestSuccess(String response) {
                MainItem mainItem = new Gson().fromJson(response, MainItem.class);
                if (movieRecyclerView.getAdapter() == null) {
                    movieRecyclerView.setAdapter(new MovieRecyclerAdapter(MainActivity.this, mainItem));
                }
                updateRecyclerViewAdapter(mainItem, true);
            }

            @Override
            public void onRequestFailed(Exception e) {
                Log.e(TAG, e.toString());
            }
        });
    }


    private void init() {
        movieRecyclerView = findViewById(R.id.movies_recycler_view);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this);
        movieRecyclerView.setLayoutManager(linearLayoutManager);
        progressBar = findViewById(R.id.progress_bar);
        preferenceUtils = new PreferencesUtils(this);
        dbHelper = new DatabaseHelper(this);
        watched = dbHelper.getWatchedMovies();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RecyclerView.LayoutManager manager = new GridLayoutManager(this, 2);
            movieRecyclerView.setLayoutManager(manager);
            movieRecyclerView.getAdapter().notifyDataSetChanged();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
            movieRecyclerView.setLayoutManager(manager);
            movieRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume(); // ????
//        if (movieRecyclerView != null && preferenceUtils != null) {
//            this.watched = dbHelper.getWatchedMovies();
//            MovieRecyclerAdapter adapter = (MovieRecyclerAdapter) movieRecyclerView.getAdapter();
//            List<MovieItem> items = adapter.getList();
//
//            adapter.refreshAdapter(setWatchedMovies(items));
//        }
    }

    private void loadMovieList(final int page, int categoryId, ApiCallbacks callback) {
        ApiService.getMovieList(page, categoryId, callback);
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
        if (!preferenceUtils.isDisplayWatched()) {
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
        boolean isDisplayWatched = preferenceUtils.isDisplayWatched();
        showWatchedCheckbox.setChecked(isDisplayWatched);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_display_watched:
                if (item.isChecked()) {
                    item.setChecked(false);
                    preferenceUtils.setDisplayWatched(false);
                    movieRecyclerView.getAdapter().notifyDataSetChanged();

                } else {
                    item.setChecked(true);
                    preferenceUtils.setDisplayWatched(true);
                    movieRecyclerView.getAdapter().notifyDataSetChanged();

                }
                this.watched = preferenceUtils.getWatchedMovies();
//                ((MovieRecyclerAdapter)movieRecyclerView.getAdapter()).refreshAdapter(new ArrayList<MovieItem>());
//                loadMovieList(); // ?????????
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateRecyclerViewAdapter(MainItem mainitem, boolean replaceData) {
        ((MovieRecyclerAdapter) movieRecyclerView.getAdapter()).refreshAdapter(mainitem, replaceData);
        progressBar.setVisibility(View.GONE);
    }

    private void clearMovieAdapterData() {
        ((MovieRecyclerAdapter) movieRecyclerView.getAdapter()).clearAllData();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void loadNextPage(int page, int totalPages) {
        if (page < totalPages) {
            ApiService.getMovieList(page, categoryId, new ApiCallbacks() {
                @Override
                public void onRequestSuccess(String response) {
                    MainItem mainItem = new Gson().fromJson(response, MainItem.class);
                    updateRecyclerViewAdapter(mainItem, false);
                }

                @Override
                public void onRequestFailed(Exception e) {

                }
            });
        }
    }

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
                    MainItem item = new Gson().fromJson(response, MainItem.class);
                    updateRecyclerViewAdapter(item, true);
                }

                @Override
                public void onRequestFailed(Exception e) {
                    Log.e(TAG, e.toString());
                }
            });
        } else {

            clearMovieAdapterData();
            ApiService.getMovieList(1, categoryId, new ApiCallbacks() {
                @Override
                public void onRequestSuccess(String response) {
                    updateRecyclerViewAdapter(new Gson().fromJson(response, MainItem.class), true);
                }

                @Override
                public void onRequestFailed(Exception e) {

                }
            });
        }
        return true;
    }
}

