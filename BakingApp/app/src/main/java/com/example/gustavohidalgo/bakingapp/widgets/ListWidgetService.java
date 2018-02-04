package com.example.gustavohidalgo.bakingapp.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by hdant on 03/02/2018.
 */

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return(new ListRemoteViewsFactory(this.getApplicationContext(), intent));
    }
}
