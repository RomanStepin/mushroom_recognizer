package com.stepin.mushroom_recognizer;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget_places extends AppWidgetProvider {

    static SQLiteDatabase base;
    static Cursor cursor;
    static double coordinate_x;
    static double coordinate_y;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.d("LOG", "updateAppWidget");
        DB_places_helper db_places_helper = new DB_places_helper(context, "MyDB_places", null, 1);
        base = db_places_helper.getReadableDatabase();
        cursor = base.query("mushroom_places", null, null, null, null, null, null);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_places);

        Intent intent = new Intent(context, MyWidgetList_service.class);
        intent.setAction("create_list");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.listViewWidget_places, intent);

        Intent intent_update = new Intent(context, AppWidget_places.class);
        intent_update.setAction("update");
        intent_update.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent_update = PendingIntent.getBroadcast(context, appWidgetId, intent_update, 0);
        views.setOnClickPendingIntent(R.id.appwidget_update, pendingIntent_update);

        Intent intent_item_click = new Intent(context, AppWidget_places.class);
        intent_item_click.setAction("item_click");
        intent_item_click.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent_item_click = PendingIntent.getBroadcast(context, appWidgetId, intent_item_click, 0);
        views.setPendingIntentTemplate(R.id.listViewWidget_places, pendingIntent_item_click);


      //  intent_activity_open.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
       // PendingIntent pendingIntent_activity_open = PendingIntent.getActivity(context, appWidgetId, intent_activity_open, 0);
       // views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent_activity_open);


        SharedPreferences sharedPreferences_coordinates_from_widget = context.getSharedPreferences("sharedPreferences_coordinates_from_widget" + appWidgetId, MODE_PRIVATE);
        Log.d("LOG", "WIDGET  coordinate_x = " + sharedPreferences_coordinates_from_widget.getFloat("coordinate_x", 0));
        Log.d("LOG", "WIDGET   coordinate_y = " + sharedPreferences_coordinates_from_widget.getFloat("coordinate_y", 0));
        coordinate_x = sharedPreferences_coordinates_from_widget.getFloat("coordinate_x", 0);
        coordinate_y = sharedPreferences_coordinates_from_widget.getFloat("coordinate_y", 0);

        // Instruct the widget manager to update the widget
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listViewWidget_places);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
        if(intent.getAction().equals("item_click"))
        {
            Intent intent_for_maps = new Intent();
            intent_for_maps.setAction(Intent.ACTION_VIEW);
            String x =  String.valueOf(intent.getDoubleExtra("coordinate_x", 0));
            String y =  String.valueOf(intent.getDoubleExtra("coordinate_y", 0));
            intent_for_maps.setData(Uri.parse("geo:" + x + ", "  + y));
            intent_for_maps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent_for_maps);
            AppWidget_places.updateAppWidget(context, AppWidgetManager.getInstance(context), intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID));
        }
        if(intent.getAction().equals("update")) {
            Intent intent_activity_open = new Intent(context, activity_widget_coordinates.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent_activity_open.setAction("item_activity_start");
            intent_activity_open.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            intent_activity_open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent_activity_open);
            AppWidget_places.updateAppWidget(context, AppWidgetManager.getInstance(context), intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID));
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("LOG", "onUpdate");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.d("LOG", "onEnabled");

        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        Log.d("LOG", "onDisabled");
        // Enter relevant functionality for when the last widget is disabled
    }

}

class MyWidgetList_factory implements RemoteViewsService.RemoteViewsFactory
{
    Context context;
    Intent intent;
    Cursor cursor;


    MyWidgetList_factory(Context context, Intent intent)
    {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        cursor = AppWidget_places.cursor;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return AppWidget_places.cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item_layout);
        cursor.moveToPosition(position);
        String place_desc = cursor.getString(cursor.getColumnIndex("description"));
        double x = cursor.getDouble(cursor.getColumnIndex("coordinate_x"));
        double y = cursor.getDouble(cursor.getColumnIndex("coordinate_y"));
        remoteViews.setTextViewText(R.id.textViewItemText, place_desc);
        Intent intent = new Intent();
        intent.setAction("item_click");
        intent.putExtra("coordinate_x", x);
        intent.putExtra("coordinate_y", y);

        Log.d("LOG", "" + position + "GET_VIEW_AT  coordinate_x = " + AppWidget_places.coordinate_x);
        Log.d("LOG", "" + position + "GET_VIEW_AT  coordinate_y = " + AppWidget_places.coordinate_y);
        Log.d("LOG", "" + position + "GET_VIEW_AT  coordinate_x1 = " + x);
        Log.d("LOG", "" + position + "GET_VIEW_AT  coordinate_y1 = " + y);

        double s = Math.acos(Math.sin(Math.toRadians(AppWidget_places.coordinate_x)) * Math.sin(Math.toRadians(x)) + Math.cos(Math.toRadians(AppWidget_places.coordinate_x)) * Math.cos(Math.toRadians(x)) * Math.cos(Math.toRadians(AppWidget_places.coordinate_y) - Math.toRadians(y))) * 6371;// * 6371 * 3.141592/180;
        Log.d("LOG", "S = " + s);
        remoteViews.setTextViewText(R.id.textViewItemS, "Расстояние: " + (int)s + "км. " + (int)((s - (int)s) * 1000) + "м.");
        remoteViews.setOnClickFillInIntent(R.id.widget_item_layout, intent);
        return remoteViews;
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
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

