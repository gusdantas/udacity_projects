package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {
    private JSONArray mTrailersList;
    private String mVideoID;
    private final Context mContext;

    public TrailersAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.movie_item, parent, false);
        return new TrailerViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        mVideoID = "";
        try {
            mVideoID = mTrailersList.getJSONObject(position).getString("source");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String posterUrl = "http://img.youtube.com/vi/" + mVideoID + "/mqdefault.jpg";
        Picasso.with(mContext)
                .load(posterUrl)
                .into(holder.mMovieThumbImageView);
    }

    @Override
    public int getItemCount() {
        if (mTrailersList != null) {
            return mTrailersList.length();
        } else {
            return 0;
        }
    }

    public void setTrailersList(JSONArray trailersList) {
        this.mTrailersList = trailersList;
        notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mMovieThumbImageView;
        final Context mContext;

        TrailerViewHolder(Context context, View itemView) {
            super (itemView);
            mContext = context;
            mMovieThumbImageView = itemView.findViewById(R.id.iv_image_thumb);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + mVideoID));
            mContext.startActivity(intent);
        }
    }
}
