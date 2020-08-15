package com.stepin.mushroom_recognizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class activity_widget_coordinates extends AppCompatActivity {
    LocationManager locationManager;
    int id;
    Button button_ok;
    Button button_cancel;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_coordinates);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Intent intent = getIntent();
        id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        button_cancel = findViewById(R.id.button_widget_coordinates_cancel);
        button_ok = findViewById(R.id.button_widget_coordinates_ok);
        setResult(RESULT_CANCELED, intent);
        context = this;
       // if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) & !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
         //   showDialog(1);

      //  LocationRequest();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) & !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            showDialog(1);

        LocationRequest();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        for (int i = 0 ; i< grantResults.length; i++) {
            Log.d("LOG", "" + grantResults[i]);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, locationListener);

           // onCreateDialog(1);
        }
        else
        {
            for (int i = 0 ; i < grantResults.length; i++) {
                if (grantResults[i] == -1);
                Intent intent = getIntent();
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        }
    }

    void LocationRequest()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  }, PackageManager.PERMISSION_GRANTED);
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  }, PackageManager.PERMISSION_GRANTED);
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, locationListener);
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //SharedPreferences sharedPreferences_coordinates_from_widget = getSharedPreferences("sharedPreferences_coordinates_from_widget" + id, MODE_PRIVATE);
           // sharedPreferences_coordinates_from_widget.edit().putFloat("coordinate_y", (float)location.getLongitude()).putFloat("coordinate_x", (float)location.getLatitude()).apply();
            if (location.getLongitude() != 0.0 & location.getLatitude() != 0.0)
            {
                SharedPreferences sharedPreferences_coordinates_from_widget = getSharedPreferences("sharedPreferences_coordinates_from_widget" + id, MODE_PRIVATE);
                sharedPreferences_coordinates_from_widget.edit().putFloat("coordinate_y", (float)location.getLongitude()).putFloat("coordinate_x", (float)location.getLatitude()).apply();
                Log.d("LOG", "ACTIVITY  coordinate_x = " + sharedPreferences_coordinates_from_widget.getFloat("coordinate_x", 0));
                Log.d("LOG", "ACTIVITY coordinate_y = " + sharedPreferences_coordinates_from_widget.getFloat("coordinate_y", 0));
                try {
                   // dismissDialog(1);
                }
                catch (IllegalArgumentException e) {}
                Intent intent = getIntent();
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
                setResult(RESULT_OK, intent);
                AppWidget_places.updateAppWidget(context,AppWidgetManager.getInstance(context),id);
                finish();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("LOG", "onStatusChanged");

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("LOG", "onProviderEnabled");

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("LOG", "onProviderDisabled");

        }
    };

    @Override
    protected Dialog onCreateDialog(int id_this) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Включить GPS навигацию ?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = getIntent();
                setResult(RESULT_CANCELED, intent);
                AppWidget_places.updateAppWidget(context,AppWidgetManager.getInstance(context), id);
                finish();
            }
        });
        return builder.create();
    }


}
