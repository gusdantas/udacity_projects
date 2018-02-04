package com.example.gustavohidalgo.bakingapp.widgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.adapters.RecipeAdapter;
import com.example.gustavohidalgo.bakingapp.interfaces.OnAdapterToDetailListener;
import com.example.gustavohidalgo.bakingapp.utils.Measure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.gustavohidalgo.bakingapp.activities.MainActivity.JSON_URL;

/**
 * The configuration screen for the {@link IngredientsAppWidget IngredientsAppWidget} AppWidget.
 */
public class IngredientsAppWidgetConfigureActivity extends Activity implements OnAdapterToDetailListener {

    private static final String PREFS_NAME = "com.example.gustavohidalgo.bakingapp.widget.IngredientsAppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @BindView(R.id.recipes)
    RecyclerView mRecipesRV;
    private JSONArray mRecipeList = new JSONArray();
    RequestQueue mQueue;
    private RecipeAdapter mRecipeAdapter;

    public IngredientsAppWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    private static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

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
        mRecipesRV.setHasFixedSize(true);

        mRecipeAdapter = new RecipeAdapter(this);
        mQueue.add(createJsonObjectRequest(JSON_URL));
        mRecipeAdapter.setListener(this);
        mRecipesRV.setAdapter(mRecipeAdapter);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    @Override
    public void onStepChosen(int stepIndex) {

        final Context context = IngredientsAppWidgetConfigureActivity.this;
        StringBuilder widgetText = new StringBuilder();

        // When the button is clicked, store the string locally
        JSONArray ingredientList = null;
        try {
            ingredientList = mRecipeList.getJSONObject(stepIndex).getJSONArray("ingredients");
            widgetText.append(mRecipeList.getJSONObject(stepIndex).getString("id")).append(";")
                    .append(mRecipeList.getJSONObject(stepIndex).getString("name")).append(";");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < ingredientList.length(); i++){
            JSONObject ingredient;
            try {
                ingredient = new JSONObject(ingredientList.get(i).toString());
                widgetText.append(" - ")
                        .append(Measure.getMeasure(ingredient.getString("measure"),
                                ingredient.getDouble("quantity")))
                        .append(ingredient.getString("ingredient")).append(";");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        saveTitlePref(context, mAppWidgetId, widgetText.toString());

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        IngredientsAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId, mRecipeList);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private JsonArrayRequest createJsonObjectRequest(String urlRequest){
        return new JsonArrayRequest
                (Request.Method.GET, urlRequest, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        mRecipeList = response;
                        mRecipeAdapter.setRecipeList(mRecipeList);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
    }
}
