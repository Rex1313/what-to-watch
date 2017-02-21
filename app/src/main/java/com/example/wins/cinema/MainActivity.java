package com.example.wins.cinema;

import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ProgressBar;

import com.example.wins.cinema.adapters.MovieRecyclerAdapter;
import com.example.wins.cinema.api_transactions.ApiTask;

import com.example.wins.cinema.interfaces.ApiCallbacks;
import com.example.wins.cinema.interfaces.MovieRecyclerInteractions;
import com.example.wins.cinema.models.MainItem;
import com.example.wins.cinema.models.MovieItem;
import com.example.wins.cinema.utils.StaticValues;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements MovieRecyclerInteractions{
    private RecyclerView movieRecyclerView;
    private List<MovieItem> currentList = new ArrayList<>();
    private MovieRecyclerAdapter adapter;
    private GlobalApplication app;
    ProgressBar mProgressBar;
    SearchView searchView;
    private Gson gson = new Gson();
    private int selectedCategory;
    private int page = 1;
    private int totalPages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        selectedCategory = getIntent().getIntExtra("category", 0);
        app = (GlobalApplication) getApplicationContext();
        movieRecyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        adapter = new MovieRecyclerAdapter(this, currentList);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this);
        movieRecyclerView.setLayoutManager(linearLayoutManager);
        movieRecyclerView.setAdapter(adapter);
        int resFrameID = getResources().getIdentifier(12, "id", "com.gladiatorgames.trainyourbrain");
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        loadMovieList();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            RecyclerView.LayoutManager manager = new GridLayoutManager(this, 2);
            movieRecyclerView.setLayoutManager(manager);
            movieRecyclerView.getAdapter().notifyDataSetChanged();
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
            movieRecyclerView.setLayoutManager(manager);
            movieRecyclerView.getAdapter().notifyDataSetChanged();
        }
        for(int i =0; i<10; i++)
        {
            for(int j =0; j<10; j++)
            {
                String dupa = "aa"+i+j;
                System.out.println(dupa);
            }
        }

    }

    private void loadMovieList() {
        ApiTask movieList = new ApiTask.Builder()
                .url(StaticValues.TMDB_API_URL)
                .apiKey(StaticValues.TMDB_API_KEY)
                .appendPath("discover")
                .appendPath("movie")
                .arg("sort_by", "popularity.desc")
                .arg("with_genres", String.valueOf(selectedCategory))
                .arg("page", String.valueOf(page))
                .addProgressBar(mProgressBar)
                .build();


        movieList.start(new ApiCallbacks() {
            @Override
            public void onRequestSuccess(String response) {

                MainItem item = gson.fromJson(response, MainItem.class);
                totalPages = item.getTotalPages();
                adapter.appendAdapterData(item.getResults());
                getSupportActionBar().setTitle(String.valueOf(page)+"/"+String.valueOf(totalPages));
            }

            @Override
            public void onRequestFailed(Exception e) {
                Log.e("JSON FAILED: ", e.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 0) {
                    ApiTask movieList = new ApiTask.Builder()
                            .url(StaticValues.TMDB_API_URL)
                            .apiKey(StaticValues.TMDB_API_KEY)
                            .appendPath("search")
                            .appendPath("movie")
                            .arg("query", newText)
                            .addProgressBar(mProgressBar)
                            .build();


                    movieList.start(new ApiCallbacks() {
                        @Override
                        public void onRequestSuccess(String response) {

                            MainItem item = gson.fromJson(response, MainItem.class);
                            adapter.refreshAdapter(item.getResults());

                        }

                        @Override
                        public void onRequestFailed(Exception e) {
                            Log.e("JSON FAILED: ", e.toString());
                        }
                    });
                } else {
                    adapter.refreshAdapter(new ArrayList<MovieItem>());
                    loadMovieList();
                }
                return true;

            }

        });
        return true;

    }

    @Override
    public void loadNextPage() {
        if(page<totalPages) {
            page++;
            loadMovieList();
        }
    }
}

