package com.example.gustavohidalgo.bakingapp.view;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.interfaces.OnDetailToRecipeListener;

import org.json.JSONException;
import org.json.JSONObject;

public class RecipeActivity extends AppCompatActivity implements OnDetailToRecipeListener {
    FragmentManager mFragmentManager;
    JSONObject mRecipe;
    private int mLastStepIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Intent intent = getIntent();
        String recipe = intent.getStringExtra("recipe");
        try {
            mRecipe = new JSONObject(recipe);
            mLastStepIndex = mRecipe.getJSONArray("steps").length() - 1;
            getSupportActionBar().setTitle(mRecipe.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RecipeFragment recipeFragment = RecipeFragment.newInstance(recipe);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().add(R.id.recipe_activity, recipeFragment)
                .addToBackStack(null).commit();

    }

    @Override
    public void onFragmentInteraction(int stepIndex) {
        JSONObject step = null;
        try {
            step = mRecipe.getJSONArray("steps").getJSONObject(stepIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RecipeDetailFragment recipeDetailFragment = RecipeDetailFragment.newInstance(step);
        mFragmentManager.beginTransaction().replace(R.id.recipe_activity, recipeDetailFragment)
                .addToBackStack(null).commit();
    }

    public int getLastStepIndex(){
        return mLastStepIndex;
    }
}
