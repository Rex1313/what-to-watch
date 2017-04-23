package com.example.wins.cinema;

import android.content.res.Configuration;
import android.graphics.Color;
import android.media.Image;
import android.media.Rating;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wins.cinema.adapters.ViewPagerAdapter;
import com.example.wins.cinema.api_transactions.ApiTask;
import com.example.wins.cinema.core.BlurTransform;
import com.example.wins.cinema.interfaces.ApiCallbacks;
import com.example.wins.cinema.models.MovieItem;
import com.example.wins.cinema.models.OmdbMovieItem;
import com.example.wins.cinema.models.Ratings;
import com.example.wins.cinema.models.Watched;
import com.example.wins.cinema.utils.PreferencesUtils;
import com.example.wins.cinema.utils.StaticHelper;
import com.example.wins.cinema.utils.StaticValues;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailActivity extends BaseActivity implements InfoFragment.OnFragmentInteractionListener, StoryFragment.OnFragmentInteractionListener, MediaFragment.OnFragmentInteractionListener {
    ImageView backdrop, cover;
    TextView title, genre, info, duration, tomatoesRating, imdbRating, mainRating;
    private MovieItem movieItem;
    private OmdbMovieItem omdbMovieItem = new OmdbMovieItem();
    private GlobalApplication app;
    private Gson gson = new Gson();
    private ViewPagerAdapter pagerAdapter;
    private ProgressBar mProgressBar;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ApiTask additionalInfoTask;
    private CheckBox watchedCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        app = (GlobalApplication) getApplicationContext();
        movieItem = (MovieItem) getIntent().getSerializableExtra("movieItem");
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        getAdditionalInfo();
        init();
        populateLayout();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.additionalInfoTask != null) {
            this.additionalInfoTask.cancel(false);
        }
    }

    private void getAdditionalInfo() {
        additionalInfoTask = new ApiTask.Builder()
                .url(StaticValues.OMDB_BASE_URL)
                .arg("t", movieItem.getTitle())
                .arg("y", movieItem.getRelease_date().substring(0, 4))
                .arg("tomatoes", "true")
                .addProgressBar(mProgressBar)
                .build();

        additionalInfoTask.start(new ApiCallbacks() {
            @Override
            public void onRequestSuccess(String response) {
                final OmdbMovieItem item = gson.fromJson(response, OmdbMovieItem.class);
                omdbMovieItem = item;
                DetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        duration.setText(item.getRuntime());
                        // There was a change in API, tomatoes rating is available now in Ratings where source = "Rotten Tomatoes"
                        for(Ratings rating: item.getRatings()){
                            if(rating.getSource().equals("Rotten Tomatoes")){
                                tomatoesRating.setText(rating.getValue());
                            }
                        }

                        imdbRating.setText(item.getImdbRating());
                        mainRating.setText(movieItem.getVote_average());

                        InfoFragment infoFragment = (InfoFragment) pagerAdapter.getFragments().get(1);
                        infoFragment.populateInfo(item);

                    }
                });

            }

            @Override
            public void onRequestFailed(Exception e) {

            }
        });
    }

    private void init() {
        backdrop = (ImageView) findViewById(R.id.backdrop);
        title = (TextView) findViewById(R.id.title);
        genre = (TextView) findViewById(R.id.genre);
        cover = (ImageView) findViewById(R.id.movie_cover);
        Picasso.with(this).load(StaticValues.POSTER_500_BASE_URL + movieItem.getPoster_path()).fit().centerCrop().into(cover);
        duration = (TextView) findViewById(R.id.duration);
        tomatoesRating = (TextView) findViewById(R.id.tomatoes_rating);
        mainRating = (TextView) findViewById(R.id.main_rating);
        imdbRating = (TextView) findViewById(R.id.imdb_rating);
        watchedCheckbox = (CheckBox) findViewById(R.id.watched_checkbox);
        watchedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                StaticHelper.addWatched(DetailActivity.this,movieItem.getId());
                }else{
                    StaticHelper.removeWatched(DetailActivity.this, movieItem.getId());
                }
            }
        });
        watchedCheckbox.setChecked(movieItem.isWatched);
        toolbar.setTitle(movieItem.getTitle());
        // Initialization of ViewPager
        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new ViewPagerAdapter(this.getSupportFragmentManager());
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(pagerAdapter);
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.pager_header);
        mTabLayout.setupWithViewPager(pager);

    }



    private void populateLayout() {
        Picasso.with(this).load(StaticValues.POSTER_1000_BASE_URL + movieItem.getBackdrop_path()).fit().centerCrop().into(backdrop);
        title.setText(movieItem.getTitle());
        String genreString = "";
        for (int i = 0; i < movieItem.getGenre_ids().length; i++) {
            if (i != 0) {
                genreString += ", ";
            }

            genreString += app.getGenres().get(movieItem.getGenre_ids()[i]);
        }
        genre.setText(genreString);
//        info.setText(movieItem.getOverview());


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


    @Override
    public OmdbMovieItem getMovieDetails() {
        return omdbMovieItem;
    }
}
