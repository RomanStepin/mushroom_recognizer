package com.stepin.mushroom_recognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.startActivities;

public class activity_mushroom_places extends AppCompatActivity {
    ArrayList<Integer> checked_items;
    MySimpleCursorAdapter mySimpleCursorAdapter;
    ListView listView;
    DB_places_helper dataBaseHelper_places;
    public static SQLiteDatabase sqLiteDatabase_places;
    Cursor cursor_places;
    ContentValues contentValues;
    Intent intent;
    public activity_mushroom_places AMP;
    public static final int MaxPlaces = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mushroom_places);
       // deleteDatabase("MyDB_places");
        AMP = this;

        dataBaseHelper_places = new DB_places_helper(this,"MyDB_places", null, 1);
        sqLiteDatabase_places = dataBaseHelper_places.getReadableDatabase();
        cursor_places = sqLiteDatabase_places.query("mushroom_places", null,null,null,null,null,null);
        cursor_places.moveToFirst();
        listView = findViewById(R.id.listView_places);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        mySimpleCursorAdapter = new MySimpleCursorAdapter(this,android.R.layout.simple_list_item_activated_1, cursor_places,new String[]{"description"},new int[]{android.R.id.text1},BIND_AUTO_CREATE);
        listView.setMultiChoiceModeListener(multiChoiceModeListener);
        listView.setOnItemClickListener(onPlaceClickListener);
        listView.setAdapter(mySimpleCursorAdapter);
    }

    AdapterView.OnItemClickListener onPlaceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            String coordinates_from_description = view.getContentDescription().toString().substring(0, view.getContentDescription().toString().indexOf("$")-1);
            intent.setData(Uri.parse("geo:" + coordinates_from_description));
            startActivity(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LOGG", "onResume");
        cursor_places = sqLiteDatabase_places.query("mushroom_places", null, null, null, null, null, null);
        cursor_places.moveToFirst();
        mySimpleCursorAdapter.changeCursor(cursor_places);
    }

    AbsListView.MultiChoiceModeListener multiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
        Menu thismenu;
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            if (checked) checked_items.add(position);
            else if (!checked) checked_items.remove((Integer)position);
            if (listView.getCheckedItemCount() > 1)
            {
                thismenu.setGroupVisible (R.id.place_edit_group, false);
            }
            else {
                thismenu.setGroupVisible(R.id.place_edit_group, true);
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.plase_mode, menu);
            checked_items = new ArrayList<Integer>();
            thismenu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            View v = new View(activity_mushroom_places.this);
            switch (item.getItemId())
            {
                case R.id.place_edit:
                    for(int i = 0; i < checked_items.size(); i++)
                    {
                        v = listView.getAdapter().getView(checked_items.get(i), null, listView);
                    }
                    intent = new Intent(activity_mushroom_places.this, activity_place_add.class);
                    String id_string = v.getContentDescription().toString().substring(v.getContentDescription().toString().indexOf("$") + 1);
                    String coordinates = v.getContentDescription().toString().substring(0, v.getContentDescription().toString().indexOf("$")-1);
                    String X_string = coordinates.substring(0,coordinates.indexOf(",")-1);
                    String Y_string = coordinates.substring(coordinates.indexOf(",")+1);
                    String description_string = ((TextView)v.findViewById(android.R.id.text1)).getText().toString();
                    intent.putExtra("_id", Integer.parseInt(id_string));
                    intent.putExtra("X", Double.parseDouble(X_string));
                    intent.putExtra("Y", Double.parseDouble(Y_string));
                    intent.putExtra("description", description_string);
                    intent.putExtra("button_text", "Сохранить изменения");
                    intent.putExtra("if_change_coordinates", false);
                    intent.putExtra("is_for_map", false);
                    startActivityForResult(intent, 2);
                    break;
                case R.id.place_delete:
                    String[] ids_for_del = new String[checked_items.size()];
                    String whereClause = "";
                    for(int i = 0; i < checked_items.size(); i++)
                    {
                        v = listView.getAdapter().getView(checked_items.get(i), null, listView);
                        ids_for_del[i] = v.getContentDescription().toString().substring(v.getContentDescription().toString().indexOf("$") + 1);
                        Log.d("LOGG", "" + v.getContentDescription());
                        if (i == 0)
                            whereClause = whereClause + "_id = ? ";
                        else whereClause =  whereClause + "OR _id = ? ";
                    }
                    sqLiteDatabase_places.delete("mushroom_places", whereClause, ids_for_del);
                    cursor_places = sqLiteDatabase_places.query("mushroom_places", null, null, null, null, null, null);
                    cursor_places.moveToFirst();
                    mySimpleCursorAdapter.changeCursor(cursor_places);
                    break;
            }
            return true;
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("checked_items", checked_items);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        checked_items = savedInstanceState.getIntegerArrayList("checked_items");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.places_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.places_menu_item4).setVisible(false);
        menu.findItem(R.id.places_menu_item3).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.places_menu_item1:
                if (cursor_places.getCount() > MaxPlaces)
                    Toast.makeText(this, "Слишком много мест. Удалите уже созданные.", Toast.LENGTH_LONG).show();
                else {
                    intent = new Intent(this, activity_place_add.class);
                    intent.putExtra("_id", 0);
                    intent.putExtra("X", 0.0);
                    intent.putExtra("Y", 0.0);
                    intent.putExtra("description", "");
                    intent.putExtra("button_text", "Добавить место");
                    intent.putExtra("if_change_coordinates", true);
                    intent.putExtra("is_for_map", false);
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.places_menu_item2:
                finish();
                break;
            case R.id.places_menu_item3:
                intent = new Intent(this, activity_map.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.places_menu_item4:
                setResult(3);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("LOGG", "onActivityResult");
        if (resultCode == 1) {
            if (requestCode == 1) {
                super.onActivityResult(requestCode, resultCode, data);
                String description = data.getStringExtra("description");
                double X = data.getDoubleExtra("X", 0);
                double Y = data.getDoubleExtra("Y", 0);
                contentValues = new ContentValues();
                contentValues.put("description", description);
                contentValues.put("coordinate_x", X);
                contentValues.put("coordinate_y", Y);
                sqLiteDatabase_places.insert("mushroom_places", null, contentValues);
               // cursor_places = sqLiteDatabase_places.query("mushroom_places", null, null, null, null, null, null);
               // cursor_places.moveToFirst();
            }
            if (requestCode == 2)
            {
                super.onActivityResult(requestCode, resultCode, data);
                String description = data.getStringExtra("description");
                double X = data.getDoubleExtra("X", 0);
                double Y = data.getDoubleExtra("Y", 0);
                contentValues = new ContentValues();
                contentValues.put("description", description);
                contentValues.put("coordinate_x", X);
                contentValues.put("coordinate_y", Y);
                sqLiteDatabase_places.update("mushroom_places",contentValues,"_id = ?", new String[]{ String.valueOf(data.getIntExtra("_id", 0))});
               // cursor_places = sqLiteDatabase_places.query("mushroom_places", null, null, null, null, null, null);
                //cursor_places.moveToFirst();
            }
        }
        else if (resultCode == 2)
        {
            finish();
        }
    }

}

class MySimpleCursorAdapter extends SimpleCursorAdapter
{
    public MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = super.newView(context, cursor, parent);
        view.setContentDescription(cursor.getString(cursor.getColumnIndex("coordinate_x")) +
                "," + cursor.getString(cursor.getColumnIndex("coordinate_y")) +
                "$" + cursor.getString(cursor.getColumnIndex("_id")));
        return view;
    }

}


