package com.example.gustavohidalgo.bakingapp.view;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.adapter.RecipeAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecipesRV;
    RecipeAdapter mRecipeAdapter;
    JSONArray mRecipes = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecipesRV = findViewById(R.id.recipes);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mRecipesRV.setLayoutManager(gridLayoutManager);
        mRecipesRV.setHasFixedSize(true);

        mRecipeAdapter = new RecipeAdapter(this);
        try {
            mRecipes = new JSONArray(loadJSONFromAsset(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRecipeAdapter.setRecipeList(mRecipes);
        mRecipesRV.setAdapter(mRecipeAdapter);
    }

    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.baking);
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
