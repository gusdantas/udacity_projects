package com.example.android.popularmovies.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapters.ReviewsAdapter;
import com.example.android.popularmovies.adapters.TrailersAdapter;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.databinding.ActivityMovieScrollingBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static com.example.android.popularmovies.activities.MainActivity.BASE_URL_POSTER;
import static com.example.android.popularmovies.activities.MainActivity.BASE_URL_TMDB;
import static com.example.android.popularmovies.activities.MainActivity.sMovieCursor;
import static com.example.android.popularmovies.activities.MainActivity.MOVIE_LOADER_ID;
import static com.example.android.popularmovies.activities.MainActivity.POSTER_SIZE;
import static com.example.android.popularmovies.activities.MainActivity.REVIEWS;
import static com.example.android.popularmovies.activities.MainActivity.TRAILER;
import static com.example.android.popularmovies.activities.MainActivity.sCursorLoaderCallbacks;

public class MovieScrollingActivity extends AppCompatActivity {
    private static String mMovieID;
    ReviewsAdapter mReviewsAdapter;
    TrailersAdapter mTrailersAdapter;
    private JSONObject mMovieJsonObject;
    private JSONArray mMovieReviews, mMovieTrailers;
    private AlertDialog.Builder mPosterAlertDialog, mReviewsAlertDialog;
    RequestQueue mQueue;
    ActivityMovieScrollingBinding mMovieBinding;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mQueue = Volley.newRequestQueue(this);
        mMovieBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_movie_scrolling);

        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        ViewGroup.LayoutParams layoutParams = mMovieBinding.appBar.getLayoutParams();
        layoutParams.height = mDisplayMetrics.widthPixels*562/1000;
        mMovieBinding.appBar.setLayoutParams(layoutParams);

        setSupportActionBar(mMovieBinding.toolbar);
        mPosterAlertDialog = new AlertDialog.Builder(this);
        mReviewsAlertDialog = new AlertDialog.Builder(this);

        Intent intent = getIntent();
        try {
            mMovieJsonObject = new JSONObject(intent.getStringExtra("movie"));

            mMovieID = "/" + mMovieJsonObject.getInt("id");
            mQueue.add(createJsonObjectRequestForReview(setRequestUrl(REVIEWS)));
            mQueue.add(createJsonObjectRequestForTrailer(setRequestUrl(TRAILER)));

            String pop = String.format(Locale.getDefault(), "%.2f",
                    mMovieJsonObject.getDouble("popularity"));
            mMovieBinding.contentMovie.tvTitle.setText(mMovieJsonObject.getString("title"));
            mMovieBinding.contentMovie.tvVoteAvg
                    .setText(String.valueOf(mMovieJsonObject.getDouble("vote_average")));
            mMovieBinding.contentMovie.tvVoteCount
                    .setText(String.valueOf(mMovieJsonObject.getInt("vote_count")));
            mMovieBinding.contentMovie.tvPop.setText(pop);
            mMovieBinding.contentMovie.tvReleaseDate
                    .setText(mMovieJsonObject.getString("release_date"));
            mMovieBinding.contentMovie.tvOriginal
                    .setText(mMovieJsonObject.getString("original_title"));
            mMovieBinding.contentMovie.tvOverview
                    .setText(mMovieJsonObject.getString("overview"));

            String backdropUrl = BASE_URL_POSTER + POSTER_SIZE +
                    mMovieJsonObject.getString("backdrop_path");
            Picasso.with(this).load(backdropUrl).into(mMovieBinding.ivMovieDetails);
            mMovieBinding.toolbarLayout.setTitle(mMovieJsonObject.getString("title"));

            GridLayoutManager staggeredGridLayoutManager =
                    new GridLayoutManager(this, 1);
            mMovieBinding.contentMovie.rvTrailers.setLayoutManager(staggeredGridLayoutManager);
            mMovieBinding.contentMovie.rvTrailers.setHasFixedSize(true);
            mTrailersAdapter = new TrailersAdapter(this);
            mMovieBinding.contentMovie.rvTrailers.setAdapter(mTrailersAdapter);
            mReviewsAdapter = new ReviewsAdapter(this);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mMovieBinding.fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sMovieCursor.getCount() < 1) {
                    insertFavorite();
                } else {

                    int tmdbIndex = sMovieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TMDB_ID);
                    sMovieCursor.moveToFirst();
                    while (!sMovieCursor.isAfterLast()) {
                        if (sMovieCursor.getString(tmdbIndex).equals(mMovieID)) {
                            break;
                        }
                        sMovieCursor.moveToNext();
                    }

                    if (sMovieCursor.isAfterLast()){
                        insertFavorite();
                    } else if (sMovieCursor.getString(tmdbIndex).equals(mMovieID)) {
                        removeFavorite();
                    } else {
                        insertFavorite();
                    }
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onCheckReviews(View view) {
        View reviewView = getLayoutInflater()
                .inflate(R.layout.reviews, null);

        GridLayoutManager staggeredGridLayoutManager =
                new GridLayoutManager(this, 1);
        RecyclerView reviewsRecyclerView = reviewView.findViewById(R.id.rv_reviews);
        reviewsRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        reviewsRecyclerView.setHasFixedSize(true);
        reviewsRecyclerView.setAdapter(mReviewsAdapter);
        mReviewsAlertDialog.setView(reviewView)
                .setCancelable(true).create().show();
    }

    public void onCheckPoster(View view) {
        View posterView = getLayoutInflater()
                .inflate(R.layout.dialog_movie_poster, null);
        ImageView imageView = posterView.findViewById(R.id.iv_movie_poster);
        String posterUrl = null;
        try {
            posterUrl = BASE_URL_POSTER + POSTER_SIZE +
                    mMovieJsonObject.getString("poster_path");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Picasso.with(posterView.getContext()).load(posterUrl).into(imageView);
        mPosterAlertDialog.setView(posterView)
                .setCancelable(true).create().show();
    }

    private void insertFavorite(){
        // inserindo na base
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_TMDB_ID, mMovieID);
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_INFO,
                String.valueOf(mMovieJsonObject));
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW,
                String.valueOf(mMovieReviews));
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TRAILER,
                String.valueOf(mMovieTrailers));
        Uri uri = getContentResolver()
                .insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString() + " inserted", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void removeFavorite() {
        int dbIndex = sMovieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
        String stringId = Integer.toString(sMovieCursor.getInt(dbIndex));
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();
        getContentResolver().delete(uri, null, null);
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, sCursorLoaderCallbacks);
        Toast.makeText(getBaseContext(), uri.toString() + " removed", Toast.LENGTH_LONG)
                .show();
    }

    private JsonObjectRequest createJsonObjectRequestForTrailer (String urlRequest){
        return new JsonObjectRequest
                (Request.Method.GET, urlRequest, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mMovieTrailers = response.getJSONArray("youtube");
                            mTrailersAdapter.setTrailersList(mMovieTrailers);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
    }

    private JsonObjectRequest createJsonObjectRequestForReview (String urlRequest){
        return new JsonObjectRequest
                (Request.Method.GET, urlRequest, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mMovieReviews = response.getJSONArray("results");
                            mReviewsAdapter.setReviewsList(mMovieReviews);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
    }

    private static String setRequestUrl(String info){
        return BASE_URL_TMDB + mMovieID + info + BuildConfig.API_KEY;
    }
}
