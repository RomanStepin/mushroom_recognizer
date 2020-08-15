package com.stepin.mushroom_recognizer;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.stepin.mushroom_recognizer.R;
import com.google.android.gms.ads.MobileAds;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Menu menu;
    Intent intent;
    Button button_start;
    Button button_mushrooms_places;
    public static SQLiteDatabase sqLiteDatabase;
    public static Map<Integer, Integer> parameters_map;    // map с результатами выбора. изначально нули

    static {
        parameters_map = new ArrayMap<>(activity_parameter_selection.PARAMETERS_COUNT);
        parameters_map.put(0, 0); //body_shape
        parameters_map.put(1, 0); //mushroom_pulp
        parameters_map.put(2, 0); //hat_surface
        parameters_map.put(3, 0); //hymenophore_type
        parameters_map.put(4, 0); //hat_shape
        parameters_map.put(5, 0); //connection_hat_leg
        parameters_map.put(6, 0); //leg_surface
        parameters_map.put(7, 0); //position_hat_leg
        parameters_map.put(8, 0); //leg_shape
    }
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("LOG", "Main Activity onCreate");
        button_start = findViewById(R.id.button_start);
        button_start.setOnClickListener(this);
        button_mushrooms_places= findViewById(R.id.button_mushrooms_places);
        button_mushrooms_places.setOnClickListener(this);
        MobileAds.initialize(this);

    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_start:
            {
                intent = new Intent(this, activity_parameter_selection.class);
                intent.putExtra("parameter_number", 0);
                startActivity(intent);
                break;
            }
            case R.id.button_mushrooms_places:
                Intent intent = new Intent(this, activity_mushroom_places.class);
                startActivity(intent);
                break;
        }
    }




}
