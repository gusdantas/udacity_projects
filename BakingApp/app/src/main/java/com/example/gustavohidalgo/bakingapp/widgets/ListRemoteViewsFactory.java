package com.example.gustavohidalgo.bakingapp.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.gustavohidalgo.bakingapp.R;

import java.util.ArrayList;

/**
 * Created by hdant on 03/02/2018.
 */

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context ctxt=null;
    private final int appWidgetId;
    private final ArrayList<String> ingredients;

    public ListRemoteViewsFactory(Context ctxt, Intent intent) {
        this.ctxt=ctxt;
        appWidgetId=
                intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
        ingredients = intent.getStringArrayListExtra("ingredients");
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return ingredients.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews row=
                new RemoteViews(ctxt.getPackageName(), R.layout.widget_row);

        row.setTextViewText(R.id.widget_row, ingredients.get(i));

        Intent intent=new Intent();
        Bundle extras=new Bundle();

        extras.putString(IngredientsAppWidget.EXTRA_WORD, ingredients.get(i));
        extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtras(extras);
        row.setOnClickFillInIntent(R.id.widget_row, intent);

        return(row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
