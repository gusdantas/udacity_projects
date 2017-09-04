package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.activities.MovieScrollingActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import static com.example.android.popularmovies.activities.MainActivity.BASE_URL_POSTER;
import static com.example.android.popularmovies.activities.MainActivity.THUMB_POSTER_SIZE;

/**
 * Created by gustavo.hidalgo on 28/08/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private JSONArray mMoviesList;
    private final Context mContext;

    public MoviesAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        String posterFile = "";
        try {
            posterFile = mMoviesList.getJSONObject(position).getString("poster_path");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String posterUrl = BASE_URL_POSTER + THUMB_POSTER_SIZE + posterFile;
        Picasso.with(mContext)
                .load(posterUrl)
                .into(holder.mMovieThumbImageView);
    }

    @Override
    public int getItemCount() {
        if (mMoviesList != null) {
            return mMoviesList.length();
        } else {
            return 0;
        }
    }

    public void setMoviesList(JSONArray moviesList) {
        this.mMoviesList = moviesList;
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mMovieThumbImageView;
        final Context mContext;

        MovieViewHolder(Context context, View itemView) {
            super (itemView);
            mContext = context;
            mMovieThumbImageView = itemView.findViewById(R.id.iv_image_thumb);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, MovieScrollingActivity.class);
            try {
                intent.putExtra("movie", mMoviesList.getJSONObject(getAdapterPosition()).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mContext.startActivity(intent);
        }
    }
}
