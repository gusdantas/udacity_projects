package com.example.gustavohidalgo.bakingapp.activities;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.interfaces.OnDetailToRecipeListener;

import org.json.JSONException;
import org.json.JSONObject;

public class RecipeActivity extends AppCompatActivity implements OnDetailToRecipeListener {
    private static JSONObject mRecipe;
    private static int mLastStepIndex;
    RecipeFragment mRecipeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        if(savedInstanceState == null) {
            Intent intent = getIntent();
            String recipe = intent.getStringExtra("recipe");
            try {
                mRecipe = new JSONObject(recipe);
                mLastStepIndex = mRecipe.getJSONArray("steps").length() - 1;
                getSupportActionBar().setTitle(mRecipe.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mRecipeFragment = RecipeFragment.newInstance(recipe);
            mRecipeFragment.setListener(this);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_activity, mRecipeFragment, "teste").commit();
        } else {
            mRecipeFragment = (RecipeFragment) getSupportFragmentManager().findFragmentByTag("teste");
        }
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
        recipeDetailFragment.setListener(this);
        int detailContainer;
        if(getResources().getBoolean(R.bool.isTablet)){
            detailContainer = R.id.fragment_recipe_detail;
        } else {
            detailContainer = R.id.recipe_activity;
        }
        getSupportFragmentManager().popBackStack("frag2frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().addToBackStack("frag2frag")
                .replace(detailContainer, recipeDetailFragment)
                .commit();
    }

    @Override
    public int getLastStepIndex() {
        return mLastStepIndex;
    }
}
