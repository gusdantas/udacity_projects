package com.example.gustavohidalgo.bakingapp.view;

import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.interfaces.OnAdapterToDetailListener;
import com.example.gustavohidalgo.bakingapp.interfaces.OnDetailToRecipeListener;

import org.json.JSONException;
import org.json.JSONObject;

public class RecipeActivity extends AppCompatActivity {
    private static JSONObject mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        if(savedInstanceState == null) {
            Intent intent = getIntent();
            String recipe = intent.getStringExtra("recipe");
            try {
                mRecipe = new JSONObject(recipe);
                getSupportActionBar().setTitle(mRecipe.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RecipeFragment recipeFragment = RecipeFragment.newInstance(recipe);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_activity, recipeFragment).commit();
        }
    }

}
