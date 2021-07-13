package com.myapplicationdev.android.demolocationdetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String DEBUG_TAG = MainActivity.class.getSimpleName();
    private final int REQUEST_CODE = 101;
    private final int REQUEST_CODE_2 = 102;

    // Views
    private Button getLastLocationBtn, getLocationUpdateBtn, removeLocationUpdateBtn;

    // Last Location
    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        askPermission();
    }

    private void askPermission() {
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_2);
        }
    }

    private void initLocationComp() {
        // Connect to google play location services
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Location Request Settings
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000)
                .setSmallestDisplacement(100);
        // Listener for Location Updates
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    lastLocation = locationResult.getLastLocation();
                }
            }
        };
        // Start Requesting for Location Updates
        if (checkPermission()) {
            Log.d(DEBUG_TAG, "Check Permission Result: " + checkPermission());
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void initViews() {
        getLastLocationBtn = findViewById(R.id.get_last_location_button);
        getLocationUpdateBtn = findViewById(R.id.get_location_update_button);
        removeLocationUpdateBtn = findViewById(R.id.remove_location_update_button);
        getLastLocationBtn.setOnClickListener(this);
        getLocationUpdateBtn.setOnClickListener(this);
        removeLocationUpdateBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_last_location_button:
                if (lastLocation != null) {
                    double lat = lastLocation.getLatitude();
                    double lng = lastLocation.getLongitude();
                    Log.d(DEBUG_TAG, "Lat: " + lat + " Lang: " + lng);
                }
                break;
            case R.id.get_location_update_button:
                break;
            case R.id.remove_location_update_button:
//                break;
        }
    }

    private boolean checkPermission() {
        int permissionCheck_Coarse
                = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine
                = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED ||
                permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE || requestCode == REQUEST_CODE_2) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocationComp();
            }  else {
                Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}