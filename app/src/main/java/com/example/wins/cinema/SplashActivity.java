package com.example.wins.cinema;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;

import com.example.wins.cinema.adapters.CategoryRecyclerAdapter;
import com.example.wins.cinema.api_transactions.ApiTask;
import com.example.wins.cinema.interfaces.ApiCallbacks;
import com.example.wins.cinema.models.GenresItem;
import com.example.wins.cinema.models.GenresWrapper;
import com.example.wins.cinema.utils.StaticValues;
import com.google.gson.Gson;

import java.util.List;

public class SplashActivity extends BaseActivity {
    private Gson gson;
    private GlobalApplication app;
    private RecyclerView categoryRecyclerView;
    private ProgressBar progressBar;
    private CategoryRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("WhatToWatch?");
        setSupportActionBar(toolbar);
        this.gson = new Gson();
        this.app = (GlobalApplication) this.getApplicationContext();
        this.categoryRecyclerView = (RecyclerView) findViewById(R.id.category_recycler_view);
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        downloadGenres();
    }

    private void downloadGenres() {
        ApiTask genreTask = new ApiTask.Builder()
                .url(StaticValues.TMDB_GENRES_URL)
                .apiKey(StaticValues.TMDB_API_KEY)
                .addProgressBar(progressBar)
                .build();
        genreTask.start(new ApiCallbacks() {
            @Override
            public void onRequestSuccess(String response) {
                GenresWrapper genresItem = gson.fromJson(response, GenresWrapper.class);
                for (GenresItem item : genresItem.getGenres()) {
                    app.getGenres().put(item.getId(), item.getName());
                }

                loadCategoryAdapter(genresItem.getGenres());
            }

            @Override
            public void onRequestFailed(Exception e) {

            }
        });
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            RecyclerView.LayoutManager manager = new GridLayoutManager(this, 4);
            categoryRecyclerView.setLayoutManager(manager);
            adapter.notifyDataSetChanged();
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            RecyclerView.LayoutManager manager = new GridLayoutManager(this, 2);
            categoryRecyclerView.setLayoutManager(manager);
            adapter.notifyDataSetChanged();
        }


    }
    private void loadCategoryAdapter(List<GenresItem> genres) {
        adapter = new CategoryRecyclerAdapter(this, genres);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        categoryRecyclerView.setLayoutManager(gridLayoutManager);
        categoryRecyclerView.setAdapter(adapter);
    }
}
