package com.example.android.popularmovies.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static com.example.android.popularmovies.activities.MainActivity.BASE_URL_POSTER;
import static com.example.android.popularmovies.activities.MainActivity.POSTER_SIZE;

public class MovieScrollingActivity extends AppCompatActivity {
    private JSONObject mMovieJsonObject;
    private AlertDialog.Builder mPosterAlertDialog;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_scrolling);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        ViewGroup.LayoutParams layoutParams = appBarLayout.getLayoutParams();
        layoutParams.height = mDisplayMetrics.widthPixels*562/1000;
        appBarLayout.setLayoutParams(layoutParams);
        Toolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        mPosterAlertDialog = new AlertDialog.Builder(this);

        FloatingActionButton fab = findViewById(R.id.fab);

        ImageView mMovieImageView = findViewById(R.id.iv_movie_details);
        TextView mTitleMDTV = findViewById(R.id.tv_title);
        TextView mAvgMDTV = findViewById(R.id.tv_vote_avg);
        TextView mCountMDTV = findViewById(R.id.tv_vote_count);
        TextView mPopMDTV = findViewById(R.id.tv_pop);
        TextView mReleaseMDTV = findViewById(R.id.tv_release_date);
        TextView mOriginalMDTV = findViewById(R.id.tv_original);
        TextView mOverviewMDTV = findViewById(R.id.tv_overview);

        Intent intent = getIntent();
        try {
            mMovieJsonObject = new JSONObject(intent.getStringExtra("movie"));
            String pop = String.format(Locale.getDefault(), "%.2f",
                    mMovieJsonObject.getDouble("popularity"));
            mTitleMDTV.setText(mMovieJsonObject.getString("title"));
            mAvgMDTV.setText(String.valueOf(mMovieJsonObject.getDouble("vote_average")));
            mCountMDTV.setText(String.valueOf(mMovieJsonObject.getInt("vote_count")));
            mPopMDTV.setText(pop);
            mReleaseMDTV.setText(mMovieJsonObject.getString("release_date"));
            mOriginalMDTV.setText(mMovieJsonObject.getString("original_title"));
            mOverviewMDTV.setText(mMovieJsonObject.getString("overview"));

            String backdropUrl = BASE_URL_POSTER + POSTER_SIZE +
                    mMovieJsonObject.getString("backdrop_path");
            Picasso.with(this).load(backdropUrl).into(mMovieImageView);
            collapsingToolbarLayout.setTitle(mMovieJsonObject.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View posterView = getLayoutInflater().inflate(R.layout.dialog_movie_poster, null);
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
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



}
