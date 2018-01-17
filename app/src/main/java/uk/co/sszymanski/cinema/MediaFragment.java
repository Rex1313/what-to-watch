package uk.co.sszymanski.cinema;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uk.co.sszymanski.cinema.data.ApiService;
import uk.co.sszymanski.cinema.interfaces.ApiCallbacks;
import uk.co.sszymanski.cinema.pojo.MovieItem;
import uk.co.sszymanski.cinema.pojo.MovieVideoItem;
import uk.co.sszymanski.cinema.pojo.MovieVideoWrapper;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class MediaFragment extends Fragment implements YouTubePlayer.OnInitializedListener {
    private final String TAG = getClass().getSimpleName();
    private final String YOUTUBE_THUMB_PATH = "http://img.youtube.com/vi/";
    private View rootView;

    private OnFragmentInteractionListener listener;

    public MediaFragment() {
        // Required empty public constructor

    }


    public static MediaFragment newInstance(String param1, String param2) {
        return  new MediaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_media, container, false);
        if (savedInstanceState == null) {


            ApiService.getMovieVideos(listener.getMovieItem().getId(), new ApiCallbacks() {
                @Override
                public void onRequestSuccess(String response) {
                    Gson gson = new Gson();
                    final MovieVideoWrapper wrapper = gson.fromJson(response, MovieVideoWrapper.class);

                    for (MovieVideoItem item : wrapper.getResults()) {
                        if (item.getSite().equals("YouTube")) {
                        createVideoThumb(item);
                        }
                    }
                }

                @Override
                public void onRequestFailed(Exception e) {

                }
            });
        }
        return rootView;
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
        Picasso.with(getActivity()).load(YOUTUBE_THUMB_PATH+ item.getKey() + "/0.jpg").fit().centerCrop().into(screenshot);
        ImageButton button = (ImageButton) wrapper.findViewById(R.id.play_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement StoryFragmentInteractions");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        String getStory();

        MovieItem getMovieItem();
    }
}
