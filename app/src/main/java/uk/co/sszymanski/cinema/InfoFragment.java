package uk.co.sszymanski.cinema;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.sszymanski.cinema.pojo.OmdbMovieItem;


public class InfoFragment extends Fragment {
    private View rootView;

    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_info, container, false);
        // Inflate the layout for this fragment
        return rootView;
    }



    public void populateInfo(OmdbMovieItem item) {
        if (rootView != null) {
            TextView director = rootView.findViewById(R.id.director_value);
            TextView writer = rootView.findViewById(R.id.writer_value);
            TextView actors = rootView.findViewById(R.id.actors_value);
            TextView awards = rootView.findViewById(R.id.awards_value);
            director.setText(item.getDirector());
            writer.setText(item.getWriter());
            StringBuilder actorsString = new StringBuilder();
            String[] actorsArray = item.getActors()==null?item.getActors().split(","):new String[0];
            for (String actor : actorsArray) {
                actorsString.append(actor.trim()).append("\n");
            }
            actors.setText(actorsString.toString());
            awards.setText(item.getAwards());
        }
    }

}
