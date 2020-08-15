package com.stepin.mushroom_recognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.stepin.mushroom_recognizer.activity_mushroom_places.MaxPlaces;
import static com.stepin.mushroom_recognizer.activity_mushroom_places.sqLiteDatabase_places;

public class activity_map extends AppCompatActivity implements OnMapReadyCallback {
    SupportMapFragment mapFragment;
    GoogleMap map;
    CameraUpdate cameraUpdate;
    CameraPosition cameraPosition;
    Button button_map_back;
    Intent intent;
    boolean is_addble;
    Marker marker;
    MarkerOptions markerOptions;
    Cursor cursor_places;
    ContentValues contentValues;
    int i;
    double coordination_x_add, coordination_y_add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        i = 0;
        is_addble = false;
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        cameraPosition = new CameraPosition.Builder().target(new LatLng(54.145868, 35.769554)).zoom(4).build();
      //  cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (markerOptions == null) {
                    is_addble = true;
                    markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.draggable(true);
                    markerOptions.flat(false);
                    markerOptions.alpha(1);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_add_icon));
                    markerOptions.title("Новое грибное место");
                    markerOptions.snippet("Добавьте его через пункт в меню");
                    marker = map.addMarker(markerOptions);
                }
                else {marker.setPosition(latLng);}
                coordination_x_add = latLng.latitude;
                coordination_y_add = latLng.longitude;
            }
        });
            i = 0;
            cursor_places = sqLiteDatabase_places.query("mushroom_places", null, null, null, null, null, null);
            if (cursor_places.moveToFirst())
            {
                cameraPosition = new CameraPosition.Builder().target(new LatLng(cursor_places.getDouble(cursor_places.getColumnIndex("coordinate_x")),
                        cursor_places.getDouble(cursor_places.getColumnIndex("coordinate_y")))).zoom(4).build();
                cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                do
                {
                    double x = cursor_places.getDouble(cursor_places.getColumnIndex("coordinate_x"));
                    double y = cursor_places.getDouble(cursor_places.getColumnIndex("coordinate_y"));
                    String s = cursor_places.getString(cursor_places.getColumnIndex("description"));
                    map.addMarker(new MarkerOptions().title(s).position(new LatLng(x,y)).draggable(false).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)).anchor((float)0.5,(float)0.9));
                    i++;
                }
                while (cursor_places.moveToNext());
            }
            if (markerOptions == null & is_addble == true) {
                //is_addble = true;
                markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(coordination_x_add, coordination_y_add));
                markerOptions.draggable(true);
                markerOptions.flat(false);
                markerOptions.alpha(1);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_add_icon));
                markerOptions.title("Новое грибное место");
                markerOptions.snippet("Добавьте его через пункт в меню");
                marker = map.addMarker(markerOptions);
        }

        map.animateCamera(cameraUpdate);
    }

    private void init() {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.places_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.places_menu_item3).setVisible(false);
        menu.findItem(R.id.places_menu_item4).setVisible(true);
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
                    if (is_addble) {
                        intent = new Intent(this, activity_place_add.class);
                        intent.putExtra("_id", 0);
                        intent.putExtra("X", marker.getPosition().latitude);
                        intent.putExtra("Y", marker.getPosition().longitude);
                        intent.putExtra("description", "");
                        intent.putExtra("button_text", "Добавить место");
                        intent.putExtra("if_change_coordinates", false);
                        intent.putExtra("is_for_map", true);
                        startActivityForResult(intent, 1);
                    } else
                        Toast.makeText(this, "Нажмите на карте чтобы поставить ваш указатель", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.places_menu_item2:
                setResult(2);
                finish();
                break;
            case R.id.places_menu_item3:
                intent = new Intent(this, activity_map.class);
                startActivity(intent);
                break;
            case R.id.places_menu_item4:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String description = data.getStringExtra("description");
        double X = data.getDoubleExtra("X", 0);
        double Y = data.getDoubleExtra("Y", 0);
        ContentValues contentValues = new ContentValues();
        contentValues.put("description", description);
        contentValues.put("coordinate_x", X);
        contentValues.put("coordinate_y", Y);
        sqLiteDatabase_places.insert("mushroom_places", null, contentValues);
        Cursor cursor_places = sqLiteDatabase_places.query("mushroom_places", null, null, null, null, null, null);
        cursor_places.moveToFirst();
        try {
            marker.remove();
            markerOptions = null;
        } catch (NullPointerException e){}

        is_addble = false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("coordination_x_add", coordination_x_add);
        outState.putDouble("coordination_y_add", coordination_y_add);
        outState.putBoolean("is_addble", is_addble);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        coordination_x_add = savedInstanceState.getDouble("coordination_x_add");
        coordination_y_add = savedInstanceState.getDouble("coordination_y_add");
        is_addble = savedInstanceState.getBoolean("is_addble");
    }
}
