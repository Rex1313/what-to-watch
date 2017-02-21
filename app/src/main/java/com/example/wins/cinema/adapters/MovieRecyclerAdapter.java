package com.example.wins.cinema.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wins.cinema.DetailActivity;
import com.example.wins.cinema.MainActivity;
import com.example.wins.cinema.R;
import com.example.wins.cinema.api_transactions.ApiTask;
import com.example.wins.cinema.interfaces.ApiCallbacks;
import com.example.wins.cinema.interfaces.MovieRecyclerInteractions;
import com.example.wins.cinema.models.MainItem;
import com.example.wins.cinema.models.MovieItem;
import com.example.wins.cinema.utils.StaticValues;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wins on 10/13/2016.
 */
public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.MovieHolder> {
    private Context context;
    private List<MovieItem> mList = new ArrayList<>();
    private MovieRecyclerInteractions callback;
    private int lastPosition = -1;
    public MovieRecyclerAdapter(Context context, List<MovieItem> mList) {
        this.context = context;
        try {
            this.callback = (MovieRecyclerInteractions) context;
        } catch (ClassCastException e) {
            Log.e(this.getClass().getSimpleName()," Activity callig this adapter needs to implement MovieRecyclerInteractions interface");
        }
        this.mList = mList;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_card, parent, false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieHolder holder, final int position) {

        setFadeAnimation(holder.itemView, position);
        holder.title.setText(mList.get(position).getTitle());
        if (!mList.get(position).getRelease_date().equals("")) {
            holder.year.setText(mList.get(position).getRelease_date().substring(0, 4));
        }
        holder.time.setText(mList.get(position).getVote_average());
        Picasso.with(context).load(StaticValues.POSTER_500_BASE_URL + mList.get(position).getPoster_path()).fit().centerCrop().into(holder.cover);

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("movieItem", mList.get(position));
                Pair<View, String> pair = Pair.create((View)holder.mainLayout, "card");
                Pair<View, String> pair2 = Pair.create((View)holder.cover, "cover");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((MainActivity)context, pair, pair2);
                context.startActivity(intent, options.toBundle());
            }
        });
        if (position == mList.size() - 1) {
            callback.loadNextPage();
        }


    }
    @Override
    public void onViewDetachedFromWindow(MovieHolder holder)
    {
        holder.clearAnimation();
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
    private void setFadeAnimation(View view, int position) {
        if(position > lastPosition) {
            TranslateAnimation anim = new TranslateAnimation(-300, 0, 0, 0);
            anim.setDuration(300);
            view.startAnimation(anim);
            lastPosition = position;
        }
    }
    public void refreshAdapter(List<MovieItem> list) {
        this.mList = list;
        notifyDataSetChanged();
    }
    public void appendAdapterData(List<MovieItem> list) {
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public class MovieHolder extends RecyclerView.ViewHolder {
        TextView title, year, time;
        ImageView cover, backdrop;
        CardView mainLayout;

        public MovieHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            year = (TextView) itemView.findViewById(R.id.release_date);
            time = (TextView) itemView.findViewById(R.id.vote_average);
            cover = (ImageView) itemView.findViewById(R.id.movie_cover);
            backdrop = (ImageView) itemView.findViewById(R.id.backdrop);
            mainLayout = (CardView) itemView;

        }
        public void clearAnimation()
        {
            mainLayout.clearAnimation();
        }
    }
}
