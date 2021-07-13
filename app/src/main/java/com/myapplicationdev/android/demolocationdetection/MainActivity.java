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
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Location Request Settings
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000)
                .setSmallestDisplacement(100);
        // Listener for Location Updates
        locationCallback = new LocationCallback() {
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
               getLastLocation();
                break;
            case R.id.get_location_update_button:
                getLocationUpdate();
                break;
            case R.id.remove_location_update_button:
                removeLocationUpdates();
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

    private void getLastLocation() {
        if (lastLocation != null) {
            askPermission();
            double lat = lastLocation.getLatitude();
            double lng = lastLocation.getLongitude();
            Log.d(DEBUG_TAG, "Lat: " + lat + " Lang: " + lng);
            Toast.makeText(this, "Lat: " + lat + " Lang: " + lng, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Location Updates is Disabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        Toast.makeText(this, "Location Update Disabled", Toast.LENGTH_SHORT).show();
        lastLocation = null;
    }

    private void getLocationUpdate() {
        initLocationComp();
        Toast.makeText(this, "Location Update Enabled", Toast.LENGTH_SHORT).show();
    }
}