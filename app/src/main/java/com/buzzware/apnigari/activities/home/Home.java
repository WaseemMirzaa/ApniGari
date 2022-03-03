package com.buzzware.apnigari.activities.home;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.buzzware.apnigari.databinding.ActivityHomeBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.master.permissionhelper.PermissionHelper;

import org.jetbrains.annotations.NotNull;

import im.delight.android.location.SimpleLocation;

public class Home extends Activity implements OnMapReadyCallback {

    ActivityHomeBinding mBinding;

    Boolean hasLocationPermissions;

    private SimpleLocation location;

    PermissionHelper permissionHelper;

    GoogleMap mMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivityHomeBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

    }

    @Override
    protected void onResume() {

        super.onResume();

        checkPermissionsAndInit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (permissionHelper != null) {

            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    private void checkPermissionsAndInit() {

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

        permissionHelper = new PermissionHelper(this, permissions, 100);

        permissionHelper.request(new PermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionGranted() {

                hasLocationPermissions = true;

                location = new SimpleLocation(Home.this);

                hasLocationPermissions = true;

                init();

                location.beginUpdates();
            }

            @Override
            public void onIndividualPermissionGranted(String[] grantedPermission) {

                hasLocationPermissions = false;

            }

            @Override
            public void onPermissionDenied() {

                hasLocationPermissions = false;

            }

            @Override
            public void onPermissionDeniedBySystem() {

                hasLocationPermissions = false;

            }
        });

    }

    private void init() {

        mBinding.mapView.onCreate(null);

        mBinding.mapView.onResume();

        mBinding.mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {

        mMap = googleMap;

    }
}
