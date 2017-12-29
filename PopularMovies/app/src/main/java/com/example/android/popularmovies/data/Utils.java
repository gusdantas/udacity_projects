package com.example.android.popularmovies.data;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by hdant on 29/12/2017.
 */

public class Utils {
    public static JSONArray favoriteDbToArray(Cursor cursor) {
        StringBuilder favoriteString = new StringBuilder().append("[");
        JSONArray favoriteList = null;
        int infoIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_INFO);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            favoriteString.append(cursor.getString(infoIndex)).append(",");
            cursor.moveToNext();
        }
        favoriteString.deleteCharAt(favoriteString.length() - 1);
        favoriteString.append("]");
        try {
            favoriteList = new JSONArray(favoriteString.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return favoriteList;
    }
}
