package com.example.gustavohidalgo.bakingapp.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.activities.RecipeActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link IngredientsAppWidgetConfigureActivity IngredientsAppWidgetConfigureActivity}
 */
public class IngredientsAppWidget extends AppWidgetProvider {

    public static final String EXTRA_WORD = "com.commonsware.android.appwidget.lorem.WORD";
    private static JSONArray mRecipeList = new JSONArray();
    private static int mRecipeIndex;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, JSONArray recipeList) {

        mRecipeList = recipeList;
        CharSequence widgetText = IngredientsAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_app_widget);

        if(!widgetText.equals("EXAMPLE")) {
            ArrayList<String> ingredientsList = new ArrayList<>();
            Collections.addAll(ingredientsList, widgetText.toString().split(";"));
            mRecipeIndex = Integer.parseInt(ingredientsList.get(0));
            views.setTextViewText(R.id.recipe_title_tv, ingredientsList.get(1));
            ingredientsList.remove(1);
            ingredientsList.remove(0);
            Intent intent = new Intent(context, ListWidgetService.class);
            intent.putStringArrayListExtra("ingredients", ingredientsList);
            views.setRemoteAdapter(R.id.ingredient_list_lv, intent);

            Intent activityIntent = new Intent(context, IngredientsAppWidget.class);
            activityIntent.setAction("banana");
            views.setOnClickPendingIntent(R.id.recipe_title_tv,
                    PendingIntent.getBroadcast(context, 0, activityIntent, 0));
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if("banana".equals(intent.getAction())){
            Intent intent1 = new Intent(context, RecipeActivity.class);
            try {
                intent1.putExtra("recipe", mRecipeList.getJSONObject(mRecipeIndex).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            context.startActivity(intent1);
        }
    }
}

