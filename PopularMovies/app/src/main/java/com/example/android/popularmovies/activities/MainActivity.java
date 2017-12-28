package com.example.android.popularmovies.activities;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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
import com.example.android.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public static LoaderManager.LoaderCallbacks<Cursor> sCursorLoaderCallbacks;
    public static Cursor sMovieCursor;
    private MoviesAdapter mMoviesAdapter;
    private RequestQueue mQueue;
    private JSONArray mResults = new JSONArray();
    public static final String BASE_URL_POSTER = "http://image.tmdb.org/t/p";
    public static final String THUMB_POSTER_SIZE = "/w185";
    public static final String POSTER_SIZE = "/w780";
    public static final String BASE_URL_TMDB = "https://api.themoviedb.org/3/movie";
    public static final String POPULAR = "/popular";
    public static final String TOP_RATED = "/top_rated";
    public static final String TRAILER = "/trailers";
    public static final String REVIEWS = "/reviews";
    public static final int MOVIE_LOADER_ID = 16;

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, sCursorLoaderCallbacks);
    }

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

        sCursorLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<Cursor>(getApplicationContext()) {

                    // Initialize a Cursor, this will hold all the task data
                    Cursor mMovieData = null;

                    // onStartLoading() is called when a loader first starts loading data
                    @Override
                    protected void onStartLoading() {
                        if (mMovieData != null) {
                            // Delivers any previously loaded data immediately
                            deliverResult(mMovieData);
                        } else {
                            // Force a new load
                            forceLoad();
                        }
                    }

                    // loadInBackground() performs asynchronous loading of data
                    @Override
                    public Cursor loadInBackground() {
                        // Will implement to load data

                        // COMPLETED (5) Query and load all task data in the background; sort by priority
                        // [Hint] use a try/catch block to catch any errors in loading data

                        try {
                            return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                                    null,
                                    null,
                                    null,
                                    MovieContract.MovieEntry.COLUMN_TMDB_ID);

                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    // deliverResult sends the result of the load, a Cursor, to the registered listener
                    public void deliverResult(Cursor data) {
                        mMovieData = data;
                        super.deliverResult(data);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                sMovieCursor = data;

            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, sCursorLoaderCallbacks);
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
        return BASE_URL_TMDB + sortBy + BuildConfig.API_KEY;
    }

    /*@Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mMovieData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mMovieData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // COMPLETED (5) Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MovieContract.MovieEntry.COLUMN_TMDB_ID);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        sMovieCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }*/
}
