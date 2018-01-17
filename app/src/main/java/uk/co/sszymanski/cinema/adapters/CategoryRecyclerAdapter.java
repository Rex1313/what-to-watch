package uk.co.sszymanski.cinema.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.sszymanski.cinema.MainActivity;
import uk.co.sszymanski.cinema.R;
import uk.co.sszymanski.cinema.pojo.GenresItem;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rex on 10/19/2016.
 */
public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryHolder> {


    // TODO create rest endpoint
    private static final String SERVER_URL = "http://92.222.75.54/cinema/";
    public final String[] CATEGORY_IMAGE_LINKS = new String[]{
            SERVER_URL + "action.png",
            SERVER_URL + "adventure.png",
            SERVER_URL + "animation.png",
            SERVER_URL + "comedy.png",
            SERVER_URL + "crime.png",
            SERVER_URL + "documentary.png",
            SERVER_URL + "drama.png",
            SERVER_URL + "family.png",
            SERVER_URL + "fantasy.png",
            SERVER_URL + "history.png",
            SERVER_URL + "horror.png",
            SERVER_URL + "music.png",
            SERVER_URL + "mystery.png",
            SERVER_URL + "romance.png",
            SERVER_URL + "scifi.png",
            SERVER_URL + "tvmovie.png",
            SERVER_URL + "thriller.png",
            SERVER_URL + "war.png",
            SERVER_URL + "western.png"


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
        Picasso.with(context).load(CATEGORY_IMAGE_LINKS[position]).fit().into(holder.categoryIcon);
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

    class CategoryHolder extends RecyclerView.ViewHolder {
        ImageView categoryIcon;
        TextView categoryDescription;
        CardView mainLayout;

        CategoryHolder(View itemView) {
            super(itemView);
            this.categoryIcon =  itemView.findViewById(R.id.category_icon);
            this.categoryDescription =  itemView.findViewById(R.id.category_description);
            this.mainLayout =  itemView.findViewById(R.id.main_layout);
        }
    }
}
