package com.main.c_care;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class Map extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    SupportMapFragment mapFragment;
    MapView mapView;
    View view;

    private static final String TAG = "MapsFragment";
    private GoogleMap mMap;
    private int tag = 0;

    private float GEOFENCE_RADIUS = 100;
    private int LOCATION_REQUEST_CODE = 10001;

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    DatabaseHelper myDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(getActivity());
        geofenceHelper = new GeofenceHelper(getActivity());

        myDb = new DatabaseHelper(getContext());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Toast.makeText(getContext(), "Long tap on map to select home location", Toast.LENGTH_SHORT).show();
                tag = 0;
                break;
            case R.id.action_frequently:
                Toast.makeText(getContext(), "Long tap on map to select frequent location", Toast.LENGTH_SHORT).show();
                tag = 2;
                break;
            case R.id.action_hotspot:
                Toast.makeText(getContext(), "Long tap on map to select hotspot location", Toast.LENGTH_SHORT).show();
                tag = 1;
                break;
            case R.id.action_log:
                viewAllData();
                break;
            case R.id.action_reset:
                deleteLocation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.map);
        if (mapView != null)    {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
//        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        if (mapFragment == null) {
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            mapFragment = SupportMapFragment.newInstance();
//            fragmentTransaction.replace(R.id.map, mapFragment).commit();
//        }
//        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapsInitializer.initialize(getContext());
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng bbsr = new LatLng(20.264, 85.8259);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bbsr, 16));

        enableUserLocation();
        mMap.setOnMapLongClickListener(this);
        updateMap();
    }

    //////////////////////////////////////////////PERMISSIONS///////////////////////////////////////
    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
//            startService();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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
            Toast.makeText(getContext(), "DATA INSERTED", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), "DATA NOT INSERTED", Toast.LENGTH_SHORT).show();
    }

    private void updateLocation(LatLng latLng, int tag) {
        boolean isUpdated = myDb.updateData(String.valueOf(tag), latLng);
        if (!isUpdated)
            insertLocation(latLng, tag);
        else
            Toast.makeText(getContext(), "DATA UPDATED", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "RESET SUCCESSFULL", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getContext(), "RESET UNSUCCESSFULL", Toast.LENGTH_SHORT).show();
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
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

                int color[][] = {{0, 255, 0}, {255, 0, 0}, {0, 0, 255}};

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
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccessRemove: Geofence removed....");
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
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
}
