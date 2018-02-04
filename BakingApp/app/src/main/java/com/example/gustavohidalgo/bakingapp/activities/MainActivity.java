package com.example.gustavohidalgo.bakingapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.adapters.RecipeAdapter;
import com.example.gustavohidalgo.bakingapp.interfaces.OnAdapterToDetailListener;
import com.example.gustavohidalgo.bakingapp.utils.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnAdapterToDetailListener {

    @BindView(R.id.recipes)
    RecyclerView mRecipesRV;
    private JSONArray mRecipes;
    RequestQueue mQueue;
    private RecipeAdapter mRecipeAdapter;

    public static final String JSON_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mQueue = Volley.newRequestQueue(this);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet){
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            mRecipesRV.setLayoutManager(gridLayoutManager);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRecipesRV.setLayoutManager(linearLayoutManager);
        }

        mRecipeAdapter = new RecipeAdapter(this);
        mQueue.add(createJsonObjectRequest(JSON_URL));
        mRecipeAdapter.setListener(this);
        mRecipesRV.setAdapter(mRecipeAdapter);
    }

    @Override
    public void onStepChosen(int stepIndex) {
        Intent intent = new Intent(this, RecipeActivity.class);
            try {
                intent.putExtra("recipe", mRecipes.getJSONObject(stepIndex).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(intent);
    }

    private JsonArrayRequest createJsonObjectRequest(String urlRequest){
        return new JsonArrayRequest
                (Request.Method.GET, urlRequest, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        mRecipes = response;
                        mRecipeAdapter.setRecipeList(mRecipes);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
    }
}
