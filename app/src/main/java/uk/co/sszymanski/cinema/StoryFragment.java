package uk.co.sszymanski.cinema;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StoryFragmentInteractions} interface
 * to handle interaction events.
 * Use the {@link StoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoryFragment extends Fragment {


    private View rootView;

    // TODO: Rename and change types of parameters
    private String info;


    private StoryFragmentInteractions mListener;

    public StoryFragment() {
        // Required empty public constructor
    }


    public static StoryFragment newInstance() {
        return new StoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_story, container, false);
        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StoryFragmentInteractions) {
            mListener = (StoryFragmentInteractions) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement StoryFragmentInteractions");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView infoText = (TextView) view.findViewById(R.id.story_text);
        infoText.setText(mListener.getStory());
    }

    public interface StoryFragmentInteractions {
        void onFragmentInteraction(Uri uri);
        String getStory();
    }
}
