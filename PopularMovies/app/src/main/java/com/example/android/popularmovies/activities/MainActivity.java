package com.example.android.popularmovies.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.adapters.MoviesAdapter;
import com.example.android.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private MoviesAdapter mMoviesAdapter;
    private RequestQueue mQueue;
    private JSONArray mResults = new JSONArray();
    public static final String BASE_URL_POSTER = "http://image.tmdb.org/t/p/";
    public static final String THUMB_POSTER_SIZE = "w185/";
    public static final String POSTER_SIZE = "w780/";
    private static final String BASE_URL_TMDB = "https://api.themoviedb.org/3/movie/";
    private static final String POPULAR = "popular?api_key=";
    private static final String TOP_RATED = "top_rated?api_key=";
    private static final String API_KEY = BuildConfig.API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mMoviesRecyclerView = findViewById(R.id.rv_movies);

        GridLayoutManager staggeredGridLayoutManager =
                new GridLayoutManager(this, 3);
        mMoviesRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mMoviesRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this);
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);

        mQueue = Volley.newRequestQueue(this);
        mQueue.add(createJsonObjectRequest(setRequestUrl(POPULAR)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String sortBy = "";
        switch (item.getItemId()){
            case R.id.sort_popular:
                sortBy = setRequestUrl(POPULAR);
                break;
            case R.id.sort_top_rated:
                sortBy = setRequestUrl(TOP_RATED);
                break;
        }
        mQueue.add(createJsonObjectRequest(sortBy));
        item.setChecked(true);
        return true;
    }

    private JsonObjectRequest createJsonObjectRequest(String urlRequest){
        return new JsonObjectRequest
                (Request.Method.GET, urlRequest, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mResults = response.getJSONArray("results");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mMoviesAdapter.setMoviesList(mResults);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
    }

    private static String setRequestUrl(String sortBy){
        return BASE_URL_TMDB + sortBy + API_KEY;
    }
}
