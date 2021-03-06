package uk.co.sszymanski.cinema.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.sszymanski.cinema.R;


public class StoryFragment extends Fragment {


    private View rootView;

    private StoryFragmentInteractions mListener;

    public StoryFragment() {
    }


    public static StoryFragment newInstance() {
        return new StoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_story, container, false);
        return rootView;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView infoText = view.findViewById(R.id.story_text);
        infoText.setText(mListener.getStory());
    }

    public interface StoryFragmentInteractions {
        String getStory();
    }
}
