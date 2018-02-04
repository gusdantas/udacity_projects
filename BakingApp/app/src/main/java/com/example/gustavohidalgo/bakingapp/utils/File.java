package com.example.gustavohidalgo.bakingapp.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hdant on 03/02/2018.
 */

public final class File {
    public static String loadJSONFromAsset(Context context, int resource) {
        String json;
        try {
            InputStream is = context.getResources().openRawResource(resource);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
