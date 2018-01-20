package uk.co.sszymanski.cinema.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import uk.co.sszymanski.cinema.GlobalApplication;
import uk.co.sszymanski.cinema.R;
import uk.co.sszymanski.cinema.adapters.CategoryRecyclerAdapter;
import uk.co.sszymanski.cinema.data.ApiService;
import uk.co.sszymanski.cinema.interfaces.ApiCallbacks;
import uk.co.sszymanski.cinema.pojo.GenresItem;
import uk.co.sszymanski.cinema.pojo.GenresWrapper;
import uk.co.sszymanski.cinema.utils.DialogUtils;


import com.google.gson.Gson;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public class SplashActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();
    private GlobalApplication app;
    private RecyclerView categoryRecyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        String mainScreenTitle = this.getResources().getString(R.string.main_screen_title);
        toolbar.setTitle(mainScreenTitle);
        setSupportActionBar(toolbar);
        this.app = (GlobalApplication) this.getApplicationContext();
        this.categoryRecyclerView = findViewById(R.id.recycler_view_category);
        this.progressBar = findViewById(R.id.progress_bar);
        downloadGenres(app.getGenres());
    }

    private void downloadGenres(final Map<Integer, String> genresMap) {

        ApiService.getGenres(new ApiCallbacks() {
            @Override
            public void onRequestSuccess(String response) {
                GenresWrapper genresItem = new Gson().fromJson(response, GenresWrapper.class);
                for (GenresItem item : genresItem.getGenres()) {
                    genresMap.put(item.getId(), item.getName());
                }
                loadCategoryAdapter(genresItem.getGenres());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onRequestFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
                if (e instanceof UnknownHostException) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtils.getSimpleAlertDialog(getString(R.string.connection_error_message), getString(R.string.connection_error_title), SplashActivity.this).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RecyclerView.LayoutManager manager = new GridLayoutManager(this, 4);
            categoryRecyclerView.setLayoutManager(manager);
            categoryRecyclerView.getAdapter().notifyDataSetChanged();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            RecyclerView.LayoutManager manager = new GridLayoutManager(this, 2);
            categoryRecyclerView.setLayoutManager(manager);
            categoryRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void loadCategoryAdapter(List<GenresItem> genres) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        categoryRecyclerView.setLayoutManager(gridLayoutManager);
        categoryRecyclerView.setAdapter(new CategoryRecyclerAdapter(this, genres));
    }
}
