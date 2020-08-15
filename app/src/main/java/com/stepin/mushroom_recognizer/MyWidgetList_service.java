package com.stepin.mushroom_recognizer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViewsService;

public class MyWidgetList_service extends RemoteViewsService
{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyWidgetList_factory(getApplicationContext(), intent);
    }
}
