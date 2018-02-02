package com.example.gustavohidalgo.bakingapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.interfaces.OnDetailToRecipeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeDetailActivity extends AppCompatActivity implements OnDetailToRecipeListener {
    private static JSONArray mStepList;
    private static int mLastStepIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        if(savedInstanceState == null) {
            Intent intent = getIntent();
            String step_list = intent.getStringExtra("step_list");
            int index = intent.getIntExtra("step_index", 0);
            JSONObject step = null;
            try {
                mStepList = new JSONArray(step_list);
                mLastStepIndex = mStepList.length() - 1;
                step = mStepList.getJSONObject(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RecipeDetailFragment recipeFragment = RecipeDetailFragment.newInstance(step);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_activity, recipeFragment).commit();
        }
    }

    @Override
    public void onFragmentInteraction(int stepIndex) {
        JSONObject step = null;
        try {
            step = mStepList.getJSONObject(stepIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RecipeDetailFragment recipeDetailFragment = RecipeDetailFragment.newInstance(step);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipe_detail_activity, recipeDetailFragment)
                .commit();
    }

    public int getLastStepIndex(){
        return mLastStepIndex;
    }
}
