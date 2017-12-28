package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by gustavo.hidalgo on 17/12/28.
 */

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_TMDB_ID = "tmdb_id";
        public static final String COLUMN_MOVIE_INFO = "info";
        public static final String COLUMN_MOVIE_TRAILER = "trailer";
        public static final String COLUMN_MOVIE_REVIEW = "review";
    }
}
