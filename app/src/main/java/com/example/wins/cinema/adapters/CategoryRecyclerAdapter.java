package com.example.wins.cinema.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wins.cinema.MainActivity;
import com.example.wins.cinema.R;
import com.example.wins.cinema.models.GenresItem;

import java.util.List;

/**
 * Created by wins on 10/19/2016.
 */
public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryHolder> {
    public static final Integer[] CATEGORY_DRAWABLES = new Integer[]{
            R.drawable.category_action,
            R.drawable.category_adventure,
            R.drawable.category_animation,
            R.drawable.category_comedy,
            R.drawable.category_crime,
            R.drawable.category_documentary,
            R.drawable.category_drama,
            R.drawable.category_family,
            R.drawable.category_fantasy,
            R.drawable.category_history,
            R.drawable.category_horror,
            R.drawable.category_music,
            R.drawable.category_mystery,
            R.drawable.category_romance,
            R.drawable.category_science_fiction,
            R.drawable.category_tv_movie,
            R.drawable.category_thriller,
            R.drawable.category_war,
            R.drawable.category_western
    };
    private List<GenresItem> list;
    private Context context;

    public CategoryRecyclerAdapter(Context context, List<GenresItem> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_card, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryHolder holder, final int position) {
        holder.categoryIcon.setImageResource(CATEGORY_DRAWABLES[position]);
        holder.categoryDescription.setText(list.get(position).getName());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("category", list.get(position).getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {
        ImageView categoryIcon;
        TextView categoryDescription;
        CardView mainLayout;

        public CategoryHolder(View itemView) {
            super(itemView);
            this.categoryIcon = (ImageView) itemView.findViewById(R.id.category_icon);
            this.categoryDescription = (TextView) itemView.findViewById(R.id.category_description);
            this.mainLayout = (CardView) itemView.findViewById(R.id.main_layout);
        }
    }
}
