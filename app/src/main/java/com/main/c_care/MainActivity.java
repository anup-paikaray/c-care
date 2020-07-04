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

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.action_map);

        runtime_permissions();
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

    Login login = new Login();
    Map map = new Map();
    Statistics statistics = new Statistics();
    Guidelines guidelines = new Guidelines();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Log.d(TAG, "onNavigationItemSelected: ");

        switch (menuItem.getItemId())   {
            case R.id.action_statistics:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, statistics).commit();
                return true;

            case R.id.action_login:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, login).commit();
                return true;

            case R.id.action_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, map).commit();
                return true;

            case R.id.action_guidelines:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, guidelines).commit();
                return true;
        }
        return false;
    }
}
