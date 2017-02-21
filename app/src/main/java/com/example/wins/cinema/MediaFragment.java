package com.example.wins.cinema;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.wins.cinema.api_transactions.ApiTask;
import com.example.wins.cinema.interfaces.ApiCallbacks;
import com.example.wins.cinema.models.MovieItem;
import com.example.wins.cinema.models.MovieVideoItem;
import com.example.wins.cinema.models.MovieVideoWrapper;
import com.example.wins.cinema.models.OmdbMovieItem;
import com.example.wins.cinema.utils.StaticHelper;
import com.example.wins.cinema.utils.StaticValues;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.api.services.youtube.YouTube;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MediaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MediaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MediaFragment extends Fragment implements YouTubePlayer.OnInitializedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View rootView;
    private YouTubePlayerSupportFragment youTubePlayerSupportFragment;

    // TODO: Rename and change types of parameters
    private String info;


    private OnFragmentInteractionListener mListener;

    public MediaFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MediaFragment newInstance(String param1, String param2) {
        MediaFragment fragment = new MediaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            info = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_media, container, false);
        if (savedInstanceState == null) {

            ApiTask apiTask = new ApiTask.Builder()
                    .url(StaticValues.TMDB_API_URL)
                    .apiKey(StaticValues.TMDB_API_KEY)
                    .appendPath("movie")
                    .appendPath(String.valueOf(mListener.getMovieItem().getId()))
                    .appendPath("videos")
                    .build();
            apiTask.start(new ApiCallbacks() {
                @Override
                public void onRequestSuccess(String response) {
                    Gson gson = new Gson();
                    final MovieVideoWrapper wrapper = gson.fromJson(response, MovieVideoWrapper.class);

                    for (MovieVideoItem item : wrapper.getResults()) {
                        if (item.getSite().equals("YouTube")) {
                        createVideoThumb(item);
                        }
                    }

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                youTubePlayerSupportFragment.initialize(StaticValues.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
//                                    @Override
//                                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                                        if (wrapper.getResults().size() > 0) {
//                                            youTubePlayer.cueVideo(wrapper.getResults().get(0).getKey());
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//                                    }
//                                });
                            }
                        });
                    }
                }

                @Override
                public void onRequestFailed(Exception e) {

                }
            });
        }
        return rootView;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * This method creates a video thumbail for youtube video and adds it to rootView
     *
     * @param item Video item
     */
    private void createVideoThumb(final MovieVideoItem item)
    {
        RelativeLayout wrapper = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.youtube_item, null);
        TextView title = (TextView) wrapper.findViewById(R.id.title);
        title.setText(item.getName());
        ImageView screenshot = (ImageView) wrapper.findViewById(R.id.youtube_screenshot);
        Picasso.with(getActivity()).load("http://img.youtube.com/vi/" + item.getKey() + "/0.jpg").fit().centerCrop().into(screenshot);
        ImageButton button = (ImageButton) wrapper.findViewById(R.id.play_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                youTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();
//                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//                transaction.replace(R.id.youtube_layout, youTubePlayerSupportFragment).commit();
//                final LinearLayout youtubePlayer = (LinearLayout) rootView.findViewById(R.id.youtube_main_view);

//                int viewPosition = (int) (((RelativeLayout)v.getParent()).getY()+StaticHelper.dpToPx(20));
//                youtubePlayer.setY(viewPosition);
//                youtubePlayer.setZ(100);
//
//                youTubePlayerSupportFragment.initialize(StaticValues.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
//                    @Override
//                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//
//                        if(!b) {
//                            youTubePlayer.loadVideo(item.getKey());
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//                    }
//                });

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+item.getKey()));
                intent.putExtra("VIDEO_ID", item.getKey());
                intent.putExtra("force_fullscreen",true);
                getActivity().startActivity(intent);
            }
        });


        ((LinearLayout) rootView.findViewById(R.id.screenshot_wrapper)).addView(wrapper);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.cueVideo("nCgQDjiotG0");
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        String getStory();

        MovieItem getMovieItem();
    }
}
