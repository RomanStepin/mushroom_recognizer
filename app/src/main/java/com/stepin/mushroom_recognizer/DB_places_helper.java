package com.stepin.mushroom_recognizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB_places_helper extends SQLiteOpenHelper
{
    public DB_places_helper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table mushroom_places (_id integer primary key autoincrement, description string, coordinate_x double, coordinate_y double)");
        ContentValues contentValues = new ContentValues();
        contentValues.put("description", "Лисичка обыкновенная, земляника.");
        contentValues.put("coordinate_x", 54.145868);
        contentValues.put("coordinate_y", 35.769554);
        db.insert("mushroom_places",null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
