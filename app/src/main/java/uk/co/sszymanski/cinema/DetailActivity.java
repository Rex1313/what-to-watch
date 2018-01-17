package uk.co.sszymanski.cinema;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import uk.co.sszymanski.cinema.adapters.ViewPagerAdapter;
import uk.co.sszymanski.cinema.data.ApiService;
import uk.co.sszymanski.cinema.interfaces.ApiCallbacks;
import uk.co.sszymanski.cinema.pojo.MovieItem;
import uk.co.sszymanski.cinema.pojo.OmdbMovieItem;
import uk.co.sszymanski.cinema.utils.StaticHelper;
import uk.co.sszymanski.cinema.utils.StaticValues;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class DetailActivity extends BaseActivity implements StoryFragment.StoryFragmentInteractions, MediaFragment.MediaFragmentInteractions {
    private final int INFO_FRAGMENT_POSITION = 1;

    private ImageView backdrop, cover;
    private TextView title, genre, duration, tomatoesRating, imdbRating, mainRating;
    private MovieItem movieItem;
    private GlobalApplication app;
    private ViewPagerAdapter pagerAdapter;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private CheckBox watchedCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        app = (GlobalApplication) getApplicationContext();
        movieItem = (MovieItem) getIntent().getSerializableExtra("movieItem");
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
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            populateExtraInfo(omdbMovieItem);
                            populateInfoFragment((InfoFragment) pagerAdapter.getFragments().get(INFO_FRAGMENT_POSITION), omdbMovieItem);
                            progressBar.setVisibility(View.GONE);

                        }
                    });
                }
            }

            @Override
            public void onRequestFailed(Exception e) {

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

    @Override
    protected void onStop() {
        super.onStop();
    }


    private void init() {
        progressBar =  findViewById(R.id.progress_bar);
        backdrop = findViewById(R.id.backdrop);
        title =  findViewById(R.id.title);
        genre =  findViewById(R.id.genre);
        cover = findViewById(R.id.movie_cover);
        Picasso.with(this).load(StaticValues.POSTER_500_BASE_URL + movieItem.getPosterPath()).fit().centerCrop().into(cover);
        duration = findViewById(R.id.duration);
        tomatoesRating =  findViewById(R.id.tomatoes_rating);
        mainRating =  findViewById(R.id.main_rating);
        imdbRating = findViewById(R.id.imdb_rating);
        watchedCheckbox = findViewById(R.id.watched_checkbox);
        watchedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    StaticHelper.addWatched(DetailActivity.this, movieItem.getId());
                } else {
                    StaticHelper.removeWatched(DetailActivity.this, movieItem.getId());
                }
            }
        });
        watchedCheckbox.setChecked(movieItem.isWatched);
        toolbar.setTitle(movieItem.getTitle());
        // Initialization of ViewPager
        ViewPager pager =  findViewById(R.id.view_pager);
        initializeViewPager(pager);
        TabLayout tabLayout =  findViewById(R.id.pager_header);
        setTablayoutToViewpager(tabLayout, pager);
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
    public void onFragmentInteraction(Uri uri) {

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
