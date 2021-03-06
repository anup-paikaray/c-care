package com.main.c_care;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.main.c_care.geofence.LocationService;
import com.main.c_care.geofence.MapGeofence;
import com.main.c_care.user.User;
import com.main.c_care.vaccine.VaccineForm;
import com.main.c_care.vaccine.WaitingList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    public static final String TAG = "MainActivity";
    public UserData userData;

    public class UserData {
        public String name, email, phone, pid;

        public UserData() {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                name = bundle.getString("USERNAME");
                email = bundle.getString("EMAIL");
                phone = bundle.getString("PHONE");
                pid = bundle.getString("PID");
            }
        }

        public UserData(String Name, String Email, String Phone, String Pid) {
            name = Name;
            email = Email;
            phone = Phone;
            pid = Pid;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.action_map);

        runtime_permissions();
        userData = new UserData();
    }

    public void startService() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Build version >= 23 or permissions not granted yet");
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 100);
        }
        else {
            Log.d("MainActivity", "Build version < 23 or permissions already granted");
//            startService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            Log.d("MainActivity", "permission: " + permissions + ": Results " + grantResults);
//            startService();
        }
        else {
            Log.d("MainActivity", "permission denied");
            runtime_permissions();
        }
    }

    User user = new User();
//    Map map = new Map();
//    Statistics statistics = new Statistics();
//    NewsFragment news = new NewsFragment();
//    Assessment assessment = new Assessment();
    VaccineForm vaccineForm = new VaccineForm();
    WaitingList waitingList = new WaitingList();
    MapGeofence mapGeofence = new MapGeofence();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Log.d(TAG, "onNavigationItemSelected: ");

        switch (menuItem.getItemId())   {
            case R.id.action_user:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, user).commit();
                return true;

            case R.id.action_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mapGeofence).commit();
                return true;

//            case R.id.action_news:
//                getSupportFragmentManager().beginTransaction().replace(R.id.container, news).commit();
//                return true;
//
//            case R.id.action_assessment:
//                getSupportFragmentManager().beginTransaction().replace(R.id.container, assessment).commit();
//                return true;
//
//            case R.id.action_statistics:
//                getSupportFragmentManager().beginTransaction().replace(R.id.container, statistics).commit();
//                return true;

            case R.id.action_services:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, vaccineForm).commit();
                return true;

            case R.id.action_waiting:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, waitingList).commit();
                return true;
        }
        return false;
    }
}