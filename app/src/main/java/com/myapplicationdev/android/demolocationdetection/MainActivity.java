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
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String DEBUG_TAG = MainActivity.class.getSimpleName();
    private final int REQUEST_CODE = 101;

    // Views
    private Button getLastLocationBtn, getLocationUpdateBtn, removeLocationUpdateBtn;

    // Last Location
    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private boolean isUpdateEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        askPermission();
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
                    isUpdateEnabled = true;
                    Log.d(DEBUG_TAG, "SET TO TRUE");
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

    private void getLastLocation() {
        String msg = "";
        if (lastLocation != null) {
            double lat = lastLocation.getLatitude();
            double lng = lastLocation.getLongitude();
            Log.d(DEBUG_TAG, "Lat: " + lat + " Lang: " + lng);
            msg += "Lat: " + lat + " Lang: " + lng;
        }
        msg += isUpdateEnabled ?
                "\nLocation Update is currently enabled." :
                "\nLocation Update is currently disabled.";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        Toast.makeText(this, "Location Update Disabled", Toast.LENGTH_SHORT).show();
        isUpdateEnabled = false;
    }

    private void getLocationUpdate() {
        initLocationComp();
        Toast.makeText(this, "Location Update Enabled", Toast.LENGTH_SHORT).show();
    }

    // --- Permissions ---
    private void askPermission() {
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE);
            return;
        }
        initLocationComp();
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
        Log.d(DEBUG_TAG, "requestCode: " + requestCode);
        Log.d(DEBUG_TAG, "permissions: " + Arrays.toString(permissions));
        Log.d(DEBUG_TAG, "grantResults: " + Arrays.toString(grantResults));
        if (requestCode == REQUEST_CODE) {
            // All Permissions Granted
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                initLocationComp();
            }  else {
                Snackbar
                        .make(findViewById(android.R.id.content), "Location Permission was not granted",
                                Snackbar.LENGTH_INDEFINITE)
                        .setAction("View", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                askPermission();
                            }
                        })
                        .show();
            }
        }
    }
}