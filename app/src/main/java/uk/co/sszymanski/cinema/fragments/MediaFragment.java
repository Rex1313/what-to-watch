package uk.co.sszymanski.cinema.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uk.co.sszymanski.cinema.R;
import uk.co.sszymanski.cinema.data.ApiService;
import uk.co.sszymanski.cinema.interfaces.ApiCallbacks;
import uk.co.sszymanski.cinema.pojo.MovieItem;
import uk.co.sszymanski.cinema.pojo.MovieVideoItem;
import uk.co.sszymanski.cinema.pojo.MovieVideoWrapper;
import uk.co.sszymanski.cinema.utils.DialogUtils;
import uk.co.sszymanski.cinema.utils.StaticValues;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class MediaFragment extends Fragment implements YouTubePlayer.OnInitializedListener {
    private final String TAG = getClass().getSimpleName();
    private View rootView;

    private MediaFragmentInteractions listener;

    public MediaFragment() {
    }


    public static MediaFragment newInstance() {
        return  new MediaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
                    DialogUtils.displayNetworkConnectionDialog(e, getActivity());
                }
            });
        }
        return rootView;
    }

    /**
     * This method creates a video thumbnail for youtube video and adds it to rootView
     *
     * @param item Video item
     */
    private void createVideoThumb(final MovieVideoItem item)
    {
        LinearLayout container = rootView.findViewById(R.id.screenshot_wrapper);
        RelativeLayout wrapper = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.youtube_item, container, false);
        TextView title = wrapper.findViewById(R.id.title);
        title.setText(item.getName());
        ImageView screenshot =  wrapper.findViewById(R.id.youtube_screenshot);
        Picasso.with(getActivity()).load(StaticValues.YOUTUBE_THUMB_PATH+ item.getKey() + "/0.jpg").fit().centerCrop().into(screenshot);
        ImageButton button = wrapper.findViewById(R.id.play_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+item.getKey()));
                intent.putExtra("VIDEO_ID", item.getKey());
                intent.putExtra("force_fullscreen",true);
                if(getActivity()!=null) getActivity().startActivity(intent);

            }
        });
        container.addView(wrapper);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MediaFragmentInteractions) {
            listener = (MediaFragmentInteractions) context;
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
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.cueVideo("nCgQDjiotG0");
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    public interface MediaFragmentInteractions {

        MovieItem getMovieItem();
    }
}
