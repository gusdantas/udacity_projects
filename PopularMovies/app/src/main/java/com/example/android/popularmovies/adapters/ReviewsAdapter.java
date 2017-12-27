package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
    private JSONArray mReviewsList;
    private final Context mContext;

    public ReviewsAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        String author = "";
        String review = "";
        try {
            author = mReviewsList.getJSONObject(position).getString("author");
            review = mReviewsList.getJSONObject(position).getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.mAuthor.setText(author);
        holder.mReview.setText(review);
    }

    @Override
    public int getItemCount() {
        if (mReviewsList != null) {
            return mReviewsList.length();
        } else {
            return 0;
        }
    }

    public void setReviewsList(JSONArray reviewsList) {
        this.mReviewsList = reviewsList;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        final TextView mAuthor, mReview;
        final Context mContext;

        ReviewViewHolder(Context context, View itemView) {
            super (itemView);
            mContext = context;
            mAuthor = itemView.findViewById(R.id.tv_author);
            mReview = itemView.findViewById(R.id.tv_review);
        }
    }
}
