package com.main.c_care;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private int tag = 0;

    private float GEOFENCE_RADIUS = 15;
    private int LOCATION_REQUEST_CODE = 10001;

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    DatabaseHelper myDb;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        myDb = new DatabaseHelper(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng bbsr = new LatLng(20.2961, 85.8245);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bbsr, 12));

        enableUserLocation();
        mMap.setOnMapLongClickListener(this);
        updateMap();
    }

    //////////////////////////////////////////////PERMISSIONS///////////////////////////////////////
    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
//            startService();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                mMap.setMyLocationEnabled(true);
//                startService();
            } else {
                enableUserLocation();
                //We do not have the permission..
            }
        }
    }

    /////////////////////////////////////////////DATABASE///////////////////////////////////////////
    private void insertLocation(LatLng latLng, int tag) {
        boolean isInserted = myDb.insertData(latLng, String.valueOf(tag));
        if (isInserted)
            Toast.makeText(MapsActivity.this, "DATA INSERTED", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MapsActivity.this, "DATA NOT INSERTED", Toast.LENGTH_SHORT).show();
    }

    private void updateLocation(LatLng latLng, int tag) {
        boolean isUpdated = myDb.updateData(String.valueOf(tag), latLng);
        if (!isUpdated)
            insertLocation(latLng, tag);
        else
            Toast.makeText(MapsActivity.this, "DATA UPDATED", Toast.LENGTH_SHORT).show();
    }

    private void viewAllData() {
        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            showMessage("Error", "Nothing Found");
            return;
        }
        else {
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {
                buffer.append("ID: " + res.getString(0)+"\n");
                buffer.append("TIMESTAMP: " + res.getString(1)+"\n");
                buffer.append("LAT: " + res.getString(2)+"\n");
                buffer.append("LNG: " + res.getString(3)+"\n");
                buffer.append("TAG: " + res.getString(4)+"\n\n");
            }
            showMessage("Data", buffer.toString());
        }
    }

    private void deleteLocation() {
        Integer deleted = myDb.deleteAllData();
        if (deleted > 0) {
            updateMap();
            Toast.makeText(MapsActivity.this, "RESET SUCCESSFULL", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(MapsActivity.this, "RESET UNSUCCESSFULL", Toast.LENGTH_SHORT).show();
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    //////////////////////////////////////////FUSED-LOCATION////////////////////////////////////////
    public void startService() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    //////////////////////////////////////////GEOFENCING////////////////////////////////////////////
    @Override
    public void onMapLongClick(LatLng latLng) {
        if (tag == 0) {
            updateLocation(latLng, tag);
            updateMap();
        }
        else if (tag > 0) {
            insertLocation(latLng, tag);
            updateMap();
        }
    }

    private void updateMap() {
        mMap.clear();
        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            return;
        }
        else {
            PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
            removeAllGeofence(pendingIntent);

            while (res.moveToNext()) {
                LatLng latLng = new LatLng(res.getFloat(2), res.getFloat(3));
                tag = res.getInt(4);
                int id = res.getInt(0);

                int color[][] = {{0, 255, 0}, {255, 0, 0}, {250, 160, 0}};

                addGeofence(String.valueOf(tag + id * 10), latLng, GEOFENCE_RADIUS, pendingIntent);
                addCircle(latLng, GEOFENCE_RADIUS, color[tag]);
            }
            tag = -1;
        }
    }

    private void addGeofence(String id, LatLng latLng, float radius, PendingIntent pendingIntent) {
        Geofence geofence = geofenceHelper.getGeofence(id, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER
                | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccessAdd: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailureAdd: " + errorMessage);
                    }
                });
    }

    private void removeAllGeofence(PendingIntent pendingIntent) {
        geofencingClient.removeGeofences(pendingIntent)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccessRemove: Geofence removed....");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailureRemove: " + errorMessage);
                    }
                });
    }

    private void addCircle(LatLng latLng, float radius, int[] color) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, color[0], color[1], color[2]));
        circleOptions.fillColor(Color.argb(64, color[0], color[1], color[2]));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

    //////////////////////////////////////////MAIN MENU/////////////////////////////////////////////
    public void handleHome(MenuItem item) {
        Toast.makeText(this, "Long tap on map to select home location", Toast.LENGTH_SHORT).show();
        tag = 0;    //HOME
    }

    public void handleHotspot(MenuItem item) {
        Toast.makeText(this, "Long tap on map to select hotspot location", Toast.LENGTH_SHORT).show();
        tag = 1;    //HOTSPOT
    }

    public void handleFreq(MenuItem item) {
        Toast.makeText(this, "Long tap on map to select frequent location", Toast.LENGTH_SHORT).show();
        tag = 2;    //FREQ
    }

    public void handleLog(MenuItem item) {
        viewAllData();
    }

    public void handleReset(MenuItem item) {
        deleteLocation();
    }
}
