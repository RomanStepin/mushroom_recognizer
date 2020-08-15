package com.stepin.mushroom_recognizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class activity_place_add extends AppCompatActivity implements View.OnClickListener {
    Button button_determinate_coordinates;
    Button button_create_place;
    LocationManager locationManager;
   // activity_place_add APA;
    EditText editText_place_description;
    TextView textView_place_coordinates_X;
    TextView textView_place_coordinates_Y;
    double coordinate_x;
    double coordinate_y;
    double coordinate_x_final;
    double coordinate_y_final;
    String description;
    boolean if_change_coordinates;
    int id;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_add);
        button_determinate_coordinates = findViewById(R.id.button_determinate_place_coordinates);
        button_determinate_coordinates.setOnClickListener(this);
        button_create_place = findViewById(R.id.button_create_place);
        button_create_place.setOnClickListener(this);
        editText_place_description = findViewById(R.id.editText_place_description);
        textView_place_coordinates_X = findViewById(R.id.textView_place_coordinates_X);
        textView_place_coordinates_Y = findViewById(R.id.textView_place_coordinates_Y);
        intent = getIntent();
        if (intent.getBooleanExtra("is_for_map", true))
        {
            button_determinate_coordinates.setEnabled(false);
        }
        else  {button_determinate_coordinates.setEnabled(true);}

        coordinate_x = intent.getDoubleExtra("X", 0);
        coordinate_y = intent.getDoubleExtra("Y", 0);
        coordinate_x_final = coordinate_x;
        coordinate_y_final = coordinate_y;
        description = intent.getStringExtra("description");
        if_change_coordinates = intent.getBooleanExtra("if_change_coordinates", false);
        id = intent.getIntExtra("_id", 0);
        button_create_place.setText(intent.getStringExtra("button_text"));
        editText_place_description.setText(description);
        textView_place_coordinates_X.setText("Широта: " + String.format("%.6f", coordinate_x));
        textView_place_coordinates_Y.setText("Долгота: " + String.format("%.6f", coordinate_y));
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationRequest();
     // APA = this;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("coordinate_x", coordinate_x);
        outState.putDouble("coordinate_y", coordinate_y);
        outState.putDouble("coordinate_x_final", coordinate_x_final);
        outState.putDouble("coordinate_y_final", coordinate_y_final);
        outState.putString("description", editText_place_description.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        coordinate_x = savedInstanceState.getDouble("coordinate_x");
        coordinate_y = savedInstanceState.getDouble("coordinate_y");
        coordinate_x_final = savedInstanceState.getDouble("coordinate_x_final");
        coordinate_y_final = savedInstanceState.getDouble("coordinate_y_final");
        editText_place_description.setText(savedInstanceState.getString("description"));
        textView_place_coordinates_X.setText("Широта: " + String.format("%.6f", coordinate_x));
        textView_place_coordinates_Y.setText("Долгота: " + String.format("%.6f", coordinate_y));
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        for (int i = 0 ; i< grantResults.length; i++) {
            Log.d("LOG", "" + grantResults[i]);
            }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
              locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locationListener);
              locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, locationListener);
        }
        else
             {
                 for (int i = 0 ; i< grantResults.length; i++) {
                     if (grantResults[i] == -1);
                     Toast.makeText(this, "Для определения координат нужно дать разрешение приложению на доступ к вашему местоположению и включить GPS навигацию !", Toast.LENGTH_LONG).show();
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

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
          //  if ((Math.abs(location.getLatitude()) > 0) && (Math.abs(location.getLongitude()) > 0)) {
                if (if_change_coordinates) {
                    coordinate_x = location.getLatitude();
                    coordinate_y = location.getLongitude();
                    coordinate_x_final = location.getLatitude();
                    coordinate_y_final = location.getLongitude();
                    textView_place_coordinates_X.setText("Широта: " + String.format("%.6f", coordinate_x));
                    textView_place_coordinates_Y.setText("Долгота: " + String.format("%.6f", coordinate_y));

                } else {
                    coordinate_x = location.getLatitude();
                    coordinate_y = location.getLongitude();
                }
         //   }
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
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_determinate_place_coordinates: {
                LocationRequest();
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) & !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                    showDialog(1);
                else {
                        textView_place_coordinates_X.setText("Широта: " + String.format("%.6f", coordinate_x));
                        textView_place_coordinates_Y.setText("Долгота: " + String.format("%.6f", coordinate_y));

                    coordinate_x_final = coordinate_x;
                    coordinate_y_final = coordinate_y;
                }
                break;
            }
            case R.id.button_create_place: {
                description = editText_place_description.getText().toString();
                if (coordinate_x == 0 && coordinate_y == 0)
                    Toast.makeText(this, "Для добавления места необходимо определить координаты !", Toast.LENGTH_LONG).show();
                else {
                    // intent = getIntent();
                    intent.putExtra("X", coordinate_x_final);
                    intent.putExtra("Y", coordinate_y_final);
                    intent.putExtra("description", description);
                    setResult(1, intent);
                    finish();
                }
                break;
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Включить GPS навигацию ?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNeutralButton("Отмена", null);
        return builder.create();
    }



}
