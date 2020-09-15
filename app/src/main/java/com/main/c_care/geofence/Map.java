package com.main.c_care.geofence;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.main.c_care.DatabaseHelper;
import com.main.c_care.R;

public class Map extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    SupportMapFragment mapFragment;
    MapView mapView;
    View view;
    SeekBar seekBar;
    Button btn_continue;
    Button btn_cancel;
    TextView textView;
    CardView cardView;
    Circle circle;

    LatLng latLong;

    private static final String TAG = "MapsFragment";
    private GoogleMap mMap;
    private int tag = 0;

    private int geofence_radius = 100;
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

        Bundle bundle = getArguments();
        if (bundle != null) {
            Float lat = Float.valueOf(bundle.getString("lat"));
            Float lng = Float.valueOf(bundle.getString("lng"));
            LatLng position = new LatLng(lat, lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
        } else {
            LatLng position = new LatLng(20.264, 85.8259);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
        }

        enableUserLocation();
        mMap.setOnMapLongClickListener(this);

        updateMap();

        seekBar = view.findViewById(R.id.seekBar);
        btn_continue = view.findViewById(R.id.btn_continue);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        cardView = view.findViewById(R.id.cardView);
        textView = view.findViewById(R.id.latlong);

        cardView.setEnabled(false);
        cardView.setVisibility(View.INVISIBLE);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                geofence_radius = i;

                int color[][] = {{0, 255, 0}, {255, 0, 0}, {0, 0, 255}};

                if(circle != null){
                    circle.remove();
                }
                circle = addCircle(latLong, geofence_radius, color[tag]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView.setEnabled(false);
                cardView.setVisibility(View.INVISIBLE);

                if (tag == 0) {
                    updateLocation(tag, latLong, geofence_radius);
                    updateMap();
                }
                else if (tag > 0) {
                    insertLocation(tag, latLong, geofence_radius);
                    updateMap();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tag = -1;
                cardView.setEnabled(false);
                cardView.setVisibility(View.INVISIBLE);
                updateMap();
            }
        });
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

    @SuppressLint("MissingPermission")
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
    private void insertLocation(int tag, LatLng latLng, int radius) {
        boolean isInserted = myDb.insertData(tag, latLng, radius);
        if (isInserted)
            Toast.makeText(getContext(), "DATA INSERTED", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), "DATA NOT INSERTED", Toast.LENGTH_SHORT).show();
    }

    private void updateLocation(int tag, LatLng latLng, int radius) {
        boolean isUpdated = myDb.updateData(tag, latLng, radius);
        if (!isUpdated)
            insertLocation(tag, latLng, radius);
        else
            Toast.makeText(getContext(), "DATA UPDATED", Toast.LENGTH_SHORT).show();
    }

    private void viewAllData() {
        Cursor res = myDb.getLocationData();
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
                buffer.append("RAD: " + res.getString(4)+"\n");
                buffer.append("TAG: " + res.getString(5)+"\n");
                buffer.append("COUNT: " + res.getString(6)+"\n\n");
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
        if (tag != -1) {
            cardView.setVisibility(View.VISIBLE);
            cardView.setEnabled(true);
            seekBar.setProgress(0);
            latLong = latLng;
            textView.setText(String.format("Lat: %.2f", latLng.latitude) + ", " + String.format("Lng: %.2f", latLng.longitude));
        }
    }

    private void updateMap() {
        mMap.clear();
        Cursor res = myDb.getLocationData();
        if (res.getCount() == 0) {
            return;
        }
        else {
            PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
            removeAllGeofence(pendingIntent);

            while (res.moveToNext()) {
                int id = res.getInt(0);
                LatLng latLng = new LatLng(res.getFloat(2), res.getFloat(3));
                tag = res.getInt(5);
                int radius = res.getInt(4);

                int color[][] = {{0, 255, 0}, {255, 0, 0}, {0, 0, 255}};

                addGeofence(String.valueOf(tag + id * 10), latLng, radius, pendingIntent);
                addCircle(latLng, radius, color[tag]);
            }
            tag = -1;
        }
    }

    @SuppressLint("MissingPermission")
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

    private Circle addCircle(LatLng latLng, float radius, int[] color) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, color[0], color[1], color[2]));
        circleOptions.fillColor(Color.argb(64, color[0], color[1], color[2]));
        circleOptions.strokeWidth(4);
        return mMap.addCircle(circleOptions);
    }
}
