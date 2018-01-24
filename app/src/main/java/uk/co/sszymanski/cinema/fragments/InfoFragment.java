package uk.co.sszymanski.cinema.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.sszymanski.cinema.R;
import uk.co.sszymanski.cinema.pojo.OmdbMovieItem;


public class InfoFragment extends Fragment {
    private View rootView;
    @BindView(R.id.director_value)
    TextView director;
    @BindView(R.id.writer_value)
    TextView writer;
    @BindView(R.id.actors_value)
    TextView actors;
    @BindView(R.id.awards_value)
    TextView awards;

    public InfoFragment() {
    }

    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    public void populateInfo(OmdbMovieItem item) {
        if (rootView != null) {
            director.setText(item.getDirector());
            writer.setText(item.getWriter());
            StringBuilder actorsString = new StringBuilder();
            String[] actorsArray = item.getActors() != null ? item.getActors().split(",") : new String[0];
            for (String actor : actorsArray) {
                actorsString.append(actor.trim()).append("\n");
            }
            actors.setText(actorsString.toString());
            awards.setText(item.getAwards());
        }
    }




}
