package com.example.interview.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.interview.R;
import com.example.interview.activity.ActivityMovieDetails;
import com.example.interview.model.ModelMovie;
import com.example.interview.network.NetworkApiTheMovieDb;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterMovieItem extends RecyclerView.Adapter<AdapterMovieItem.ItemHolder> {
    private static final String TAG = "AdapterMovieItem";
    private List<ModelMovie> dataSet;
    private Context ctx;

    public AdapterMovieItem(List<ModelMovie> myDataset, Context ctx) {
        dataSet = myDataset;
        this.ctx = ctx;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_recycler_item, parent, false);
        ItemHolder dataObjectHolder = new ItemHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        final ModelMovie modelMovie = dataSet.get(position);
        String imageUrl = String.format(NetworkApiTheMovieDb.API_IMG_URL, modelMovie.getPosterPath());
        Picasso.with(ctx).load(imageUrl).into(holder.imageView);
        holder.titleText.setText(modelMovie.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(ctx, ActivityMovieDetails.class);
               intent.putExtra("movieGenres", modelMovie.getGenres());
               intent.putExtra("movieTitle", modelMovie.getTitle());
               intent.putExtra("movieOverview", modelMovie.getOverview());
               intent.putExtra("movieImage", modelMovie.getPosterPath());
               intent.putExtra("moviePopularity", modelMovie.getPopularity());
               intent.putExtra("movieReleaseDate", modelMovie.getReleaseDate());
               ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public List<ModelMovie> getDataSet() {
        return dataSet;
    }

    public void setDataSet(List<ModelMovie> dataSet) {
        this.dataSet = dataSet;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        private final Context context;
        TextView popularityText;
        ImageView imageView;
        LinearLayout itemView;
        TextView titleText;

        public ItemHolder(View view) {
            super(view);
            context = view.getContext();

            itemView = (LinearLayout) view.findViewById(R.id.movie_recyc_item);
            imageView = (ImageView) view.findViewById(R.id.movie_recyc_item_image);
            titleText = (TextView) view.findViewById(R.id.movie_recyc_item_title);
        }
    }
}
