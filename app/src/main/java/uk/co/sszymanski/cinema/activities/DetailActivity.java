package uk.co.sszymanski.cinema.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.sszymanski.cinema.GlobalApplication;
import uk.co.sszymanski.cinema.R;
import uk.co.sszymanski.cinema.adapters.ViewPagerAdapter;
import uk.co.sszymanski.cinema.data.ApiService;
import uk.co.sszymanski.cinema.data.DatabaseHelper;
import uk.co.sszymanski.cinema.fragments.InfoFragment;
import uk.co.sszymanski.cinema.fragments.MediaFragment;
import uk.co.sszymanski.cinema.fragments.StoryFragment;
import uk.co.sszymanski.cinema.interfaces.ApiCallbacks;
import uk.co.sszymanski.cinema.pojo.MovieItem;
import uk.co.sszymanski.cinema.pojo.OmdbMovieItem;
import uk.co.sszymanski.cinema.pojo.Watched;
import uk.co.sszymanski.cinema.utils.DialogUtils;
import uk.co.sszymanski.cinema.utils.StaticValues;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class DetailActivity extends BaseActivity implements StoryFragment.StoryFragmentInteractions, MediaFragment.MediaFragmentInteractions {
    private final int INFO_FRAGMENT_POSITION = 1;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.backdrop)
    private ImageView backdrop;
    @BindView(R.id.movie_cover)
    ImageView cover;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.genre)
    TextView genre;
    @BindView(R.id.duration)
    TextView duration;
    @BindView(R.id.tomatoes_rating)
    TextView tomatoesRating;
    @BindView(R.id.imdb_rating)
    TextView imdbRating;
    @BindView(R.id.main_rating)
    TextView mainRating;
    @BindView(R.id.watched_checkbox)
    CheckBox watchedCheckbox;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.pager_header)
    TabLayout tabLayout;
    private MovieItem movieItem;
    private GlobalApplication app;
    private ViewPagerAdapter pagerAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        app = (GlobalApplication) getApplicationContext();
        movieItem = (MovieItem) getIntent().getSerializableExtra("movieItem");
        dbHelper = new DatabaseHelper(this);
        init();
        populateLayout();
        populateExtraInfo(new Gson(), movieItem);

    }



    private void populateExtraInfo(final Gson gson, MovieItem movieItem){
        ApiService.getExtraMovieInfo(movieItem, new ApiCallbacks() {
            @Override
            public void onRequestSuccess(String response) {
                final OmdbMovieItem omdbMovieItem = gson.fromJson(response, OmdbMovieItem.class);
                if (omdbMovieItem != null) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        populateExtraInfo(omdbMovieItem);
                        populateInfoFragment((InfoFragment) pagerAdapter.getFragments().get(INFO_FRAGMENT_POSITION), omdbMovieItem);
                        progressBar.setVisibility(View.GONE);
                    });
                }
            }
            @Override
            public void onRequestFailed(Exception e) {
                DialogUtils.getSimpleAlertDialog(getString(R.string.connection_error_message, DetailActivity.this), getString(R.string.connection_error_title), DetailActivity.this);
            }
        });
    }


    private void populateExtraInfo(OmdbMovieItem item) {
        duration.setText(item.getRuntime());
        tomatoesRating.setText(item.getTomatoRating());
        imdbRating.setText(item.getImdbRating());
    }

    private void populateInfoFragment(InfoFragment fragment, OmdbMovieItem item) {
        fragment.populateInfo(item);
    }

    private void init() {
        Picasso.with(this).load(StaticValues.POSTER_500_BASE_URL + movieItem.getPosterPath()).fit().centerCrop().into(cover);
        watchedCheckbox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            Watched watched = new Watched(movieItem.getId());
            Intent intent = new Intent();
            intent.putExtra("movieId", movieItem.getId());
            if(isChecked) {
                dbHelper.addWatchedMovie(watched);
                intent.putExtra("watched", true);
                setResult(RESULT_OK, intent);
            }else {
                dbHelper.removeWatchedMovie(watched);
                intent.putExtra("watched", false);
                setResult(RESULT_OK, intent);
            }

        });
        watchedCheckbox.setChecked(movieItem.isWatched);
        toolbar.setTitle(movieItem.getTitle());
        initializeViewPager(viewPager);
        setTablayoutToViewpager(tabLayout, viewPager);
    }

    private void initializeViewPager(ViewPager viewPager){
        pagerAdapter = new ViewPagerAdapter(this, this.getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);
    }



    private void setTablayoutToViewpager(TabLayout tabLayout, ViewPager viewPager){
        tabLayout.setupWithViewPager(viewPager);
    }


    private void populateLayout() {
        Picasso.with(this).load(StaticValues.POSTER_1000_BASE_URL + movieItem.getBackdropPath()).fit().centerCrop().into(backdrop);
        title.setText(movieItem.getTitle());
        StringBuilder genreString = new StringBuilder();
        for (int i = 0; i < movieItem.getGenreIds().length; i++) {
            if (i != 0) {
                genreString.append(", ");
            }
            genreString.append(app.getGenres().get(movieItem.getGenreIds()[i]));
        }
        genre.setText(genreString.toString());
        mainRating.setText(movieItem.getVoteAverage());
    }


    @Override
    public String getStory() {
        return movieItem.getOverview();
    }

    @Override
    public MovieItem getMovieItem() {
        return this.movieItem;
    }

}
