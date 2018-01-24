package uk.co.sszymanski.cinema.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.sszymanski.cinema.R;
import uk.co.sszymanski.cinema.adapters.MovieRecyclerAdapter;

import uk.co.sszymanski.cinema.data.ApiService;
import uk.co.sszymanski.cinema.data.DatabaseHelper;
import uk.co.sszymanski.cinema.interfaces.ApiCallbacks;
import uk.co.sszymanski.cinema.interfaces.MovieRecyclerInteractions;
import uk.co.sszymanski.cinema.pojo.MainItem;
import uk.co.sszymanski.cinema.pojo.MovieItem;
import uk.co.sszymanski.cinema.pojo.Watched;
import uk.co.sszymanski.cinema.utils.DialogUtils;
import uk.co.sszymanski.cinema.utils.PreferencesUtils;
import uk.co.sszymanski.cinema.utils.StaticValues;

import com.annimon.stream.Stream;
import com.google.gson.Gson;

import java.util.List;

public class MainActivity extends BaseActivity implements MovieRecyclerInteractions, SearchView.OnQueryTextListener {
    @BindView(R.id.movies_recycler_view)
    RecyclerView movieRecyclerView;
    MovieRecyclerAdapter movieRecyclerAdapter;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private final String TAG = getClass().getSimpleName();
    private DatabaseHelper dbHelper;
    private List<Watched> watched;
    private int categoryId;
    private PreferencesUtils preferenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        categoryId = getIntent().getIntExtra("categoryId", 0);
        String categoryString = getIntent().getStringExtra("categoryName");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoryString);
        }

        init();
        ApiService.getSearchMovieList(1, categoryId, new ApiCallbacks() {
            @Override
            public void onRequestSuccess(String response) {
                MainItem mainItem = new Gson().fromJson(response, MainItem.class);
                if (movieRecyclerView.getAdapter() == null) {
                    movieRecyclerAdapter = new MovieRecyclerAdapter(MainActivity.this, mainItem);
                    movieRecyclerView.setAdapter(movieRecyclerAdapter);
                    movieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            setFirstVisiblePosition();
                        }
                    });
                }
                updateRecyclerViewAdapter(mainItem, true);
            }

            @Override
            public void onRequestFailed(Exception e) {
                Log.e(TAG, e.toString());
                DialogUtils.displayNetworkConnectionDialog(e, MainActivity.this);
            }
        });
    }

    private void init() {
        RecyclerView.LayoutManager recyclerViewLayoutManager;
        int orientation = getResources().getConfiguration().orientation;
        recyclerViewLayoutManager = orientation == Configuration.ORIENTATION_PORTRAIT
                ? new LinearLayoutManager(this)
                : new GridLayoutManager(this, 2);
        movieRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        preferenceUtils = new PreferencesUtils(this);
        dbHelper = new DatabaseHelper(this);
        watched = dbHelper.getWatchedMovies();
    }

    private void setFirstVisiblePosition() {
        if (movieRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            movieRecyclerAdapter.lastRecyclerViewPosition = ((LinearLayoutManager) movieRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        }
        if (movieRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
            movieRecyclerAdapter.lastRecyclerViewPosition = ((GridLayoutManager) movieRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        watched = getWatchedMovies(dbHelper);
        if (movieRecyclerView.getAdapter() != null) {
            watched = dbHelper.getWatchedMovies();
            movieRecyclerAdapter.notifyChange(preferenceUtils.isDisplayWatched());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "configruation changed");
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RecyclerView.LayoutManager manager = new GridLayoutManager(this, 2);
            movieRecyclerView.setLayoutManager(manager);
            movieRecyclerAdapter.notifyDataSetChanged();
            movieRecyclerView.scrollToPosition(movieRecyclerAdapter.lastRecyclerViewPosition);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
            movieRecyclerView.setLayoutManager(manager);
            movieRecyclerAdapter.notifyDataSetChanged();
            movieRecyclerView.scrollToPosition(movieRecyclerAdapter.lastRecyclerViewPosition);
        }
    }

    @Override
    protected void onDestroy() {

        Log.d(TAG, "onDestroy called");
        super.onDestroy();
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
                    movieRecyclerAdapter.notifyChange(preferenceUtils.isDisplayWatched());

                } else {
                    item.setChecked(true);
                    preferenceUtils.setDisplayWatched(true);
                    movieRecyclerAdapter.notifyChange(preferenceUtils.isDisplayWatched());

                }
                this.watched = dbHelper.getWatchedMovies();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateRecyclerViewAdapter(MainItem mainitem, boolean replaceData) {
        for (MovieItem movieItem : mainitem.getResults()) {
            for (Watched watched : watched) {
                if (watched.getId() == movieItem.getId()) {
                    movieItem.isWatched = true;
                }
            }
        }
        movieRecyclerAdapter.refreshAdapter(mainitem, replaceData, preferenceUtils.isDisplayWatched());
        progressBar.setVisibility(View.GONE);
    }

    private void clearMovieAdapterData() {
        ((MovieRecyclerAdapter) movieRecyclerView.getAdapter()).clearAllData();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void loadNextPage(int page, int totalPages) {
        if (page < totalPages) {
            ApiService.getSearchMovieList(page, categoryId, new ApiCallbacks() {
                @Override
                public void onRequestSuccess(String response) {
                    MainItem mainItem = new Gson().fromJson(response, MainItem.class);
                    updateRecyclerViewAdapter(mainItem, false);
                }

                @Override
                public void onRequestFailed(Exception e) {
                    DialogUtils.displayNetworkConnectionDialog(e, MainActivity.this);
                }
            });
        }
    }

    private List<Watched> getWatchedMovies(DatabaseHelper dbHelper) {
        return dbHelper.getWatchedMovies();
    }

    @Override
    public void addWatchedMovie(int movieId) {
        dbHelper.addWatchedMovie(new Watched(movieId));
        watched = dbHelper.getWatchedMovies();
        movieRecyclerAdapter.notifyChange(preferenceUtils.isDisplayWatched());

    }

    @Override
    public void removeWatchedMovie(int movieId) {
        dbHelper.removeWatchedMovie(new Watched(movieId));
        watched = dbHelper.getWatchedMovies();
        movieRecyclerAdapter.notifyChange(preferenceUtils.isDisplayWatched());
    }

    //This is called from MovieRecyclerView, it will start activity for result. as a result we are expecting id of the movie and watched boolean
    @Override
    public void onItemClicked(MovieRecyclerAdapter.MovieHolder holder, MovieItem item, int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("movieItem", item);
        Pair<View, String> pair = Pair.create(holder.mainLayout, "card");
        Pair<View, String> pair2 = Pair.create(holder.cover, "cover");
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair, pair2);
        startActivityForResult(intent, StaticValues.DETAIL_ACTIVITY_REQUEST_CODE, options.toBundle());
    }

    @Override
    public void onItemLongPressed(MovieItem item, int position) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If user changed watched checkbox in DetailsActivity we will get the result here and update MovieRecyclerAdapter
        if (requestCode == StaticValues.DETAIL_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            boolean watched = data.getBooleanExtra("watched", false);
            int movieId = data.getIntExtra("movieId", 0);
            Stream.of(movieRecyclerAdapter.getList())
                    .filter(movieItem -> movieItem.getId() == movieId)
                    .findFirst()
                    .ifPresent(movieItem -> movieItem.isWatched=watched);
            movieRecyclerAdapter.notifyChange(preferenceUtils.isDisplayWatched());
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!newText.isEmpty()) {
            ApiService.getSearchMovieList(newText, new ApiCallbacks() {
                @Override
                public void onRequestSuccess(String response) {
                    MainItem item = new Gson().fromJson(response, MainItem.class);
                    updateRecyclerViewAdapter(item, true);
                }

                @Override
                public void onRequestFailed(Exception e) {
                    Log.e(TAG, e.toString());
                    DialogUtils.displayNetworkConnectionDialog(e, MainActivity.this);
                }
            });
        } else {

            clearMovieAdapterData();
            ApiService.getSearchMovieList(1, categoryId, new ApiCallbacks() {
                @Override
                public void onRequestSuccess(String response) {
                    updateRecyclerViewAdapter(new Gson().fromJson(response, MainItem.class), true);
                }

                @Override
                public void onRequestFailed(Exception e) {
                    DialogUtils.displayNetworkConnectionDialog(e, MainActivity.this);
                }
            });
        }
        return true;
    }
}

