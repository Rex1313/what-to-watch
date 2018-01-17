package uk.co.sszymanski.cinema;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.sszymanski.cinema.pojo.OmdbMovieItem;


public class InfoFragment extends Fragment {
    private View rootView;

    private String info;


    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance() {
        return  new InfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_info, container, false);
        // Inflate the layout for this fragment
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public void populateInfo(OmdbMovieItem item) {
        if (rootView != null) {
            TextView director = (TextView) rootView.findViewById(R.id.director_value);
            TextView writer = (TextView) rootView.findViewById(R.id.writer_value);
            TextView actors = (TextView) rootView.findViewById(R.id.actors_value);
            TextView awards = (TextView) rootView.findViewById(R.id.awards_value);
            director.setText(item.getDirector());
            writer.setText(item.getWriter());
            StringBuilder actorsString = new StringBuilder();
            String[] actorsArray = item.getActors().split(",");
            for (String actor : actorsArray) {
                actorsString.append(actor.trim()).append("\n");
            }
            actors.setText(actorsString.toString());
            awards.setText(item.getAwards());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
