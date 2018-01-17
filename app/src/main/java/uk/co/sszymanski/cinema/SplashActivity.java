package uk.co.sszymanski.cinema;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import uk.co.sszymanski.cinema.adapters.CategoryRecyclerAdapter;
import uk.co.sszymanski.cinema.data.ApiService;
import uk.co.sszymanski.cinema.interfaces.ApiCallbacks;
import uk.co.sszymanski.cinema.pojo.GenresItem;
import uk.co.sszymanski.cinema.pojo.GenresWrapper;


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
        String mainScreenTitle = this.getResources().getString(R.string.main_screen_title);
        toolbar.setTitle(mainScreenTitle);
        setSupportActionBar(toolbar);
        this.gson = new Gson();
        this.app = (GlobalApplication) this.getApplicationContext();
        this.categoryRecyclerView = (RecyclerView) findViewById(R.id.category_recycler_view);
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        downloadGenres();
    }

    private void downloadGenres() {

        ApiService.getGenres(new ApiCallbacks() {
            @Override
            public void onRequestSuccess(String response) {
                GenresWrapper genresItem = gson.fromJson(response, GenresWrapper.class);
                for (GenresItem item : genresItem.getGenres()) {
                    app.getGenres().put(item.getId(), item.getName());
                }
                loadCategoryAdapter(genresItem.getGenres());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onRequestFailed(Exception e) {

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RecyclerView.LayoutManager manager = new GridLayoutManager(this, 4);
            categoryRecyclerView.setLayoutManager(manager);
            adapter.notifyDataSetChanged();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            RecyclerView.LayoutManager manager = new GridLayoutManager(this, 2);
            categoryRecyclerView.setLayoutManager(manager);
            adapter.notifyDataSetChanged();
        }
    }

    private void loadCategoryAdapter(List<GenresItem> genres) {
        adapter = new CategoryRecyclerAdapter(this, genres);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        categoryRecyclerView.setLayoutManager(gridLayoutManager);
        categoryRecyclerView.setAdapter(adapter);
    }
}
