package com.example.gustavohidalgo.bakingapp.view;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.interfaces.OnFragmentInteractionListener;

import org.json.JSONObject;

public class RecipeActivity extends AppCompatActivity implements OnFragmentInteractionListener {
    FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Intent intent = getIntent();
        String recipe = intent.getStringExtra("recipe");

        RecipeFragment recipeFragment = RecipeFragment.newInstance(recipe);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().add(R.id.recipe_activity, recipeFragment).commit();

    }

    @Override
    public void onFragmentInteraction(JSONObject stepDetail) {
        RecipeDetailFragment recipeDetailFragment = RecipeDetailFragment.newInstance(stepDetail);
        mFragmentManager.beginTransaction().replace(R.id.recipe_activity, recipeDetailFragment).commit();
    }
}
