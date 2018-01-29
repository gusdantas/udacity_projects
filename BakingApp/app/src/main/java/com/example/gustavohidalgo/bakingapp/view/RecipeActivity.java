package com.example.gustavohidalgo.bakingapp.view;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.gustavohidalgo.bakingapp.R;

import org.json.JSONObject;

public class RecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Intent intent = getIntent();
        String recipe = intent.getStringExtra("recipe");

        RecipeFragment recipeFragment = RecipeFragment.newInstance(recipe);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.recipe_activity, recipeFragment).commit();

    }
}
