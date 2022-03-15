package com.buzzware.apnigari.activities.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.buzzware.apnigari.activities.base.BaseNavDrawer;
import com.buzzware.apnigari.activities.base.vm.BaseViewModel;
import com.buzzware.apnigari.activities.home.dialog.SearchPlaceDialog;
import com.buzzware.apnigari.activities.home.vm.HomeViewModel;
import com.buzzware.apnigari.activities.messages.chat.Chat;
import com.buzzware.apnigari.databinding.ActivityHomeBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.master.permissionhelper.PermissionHelper;

import org.jetbrains.annotations.NotNull;

import im.delight.android.location.SimpleLocation;

public class Home extends BaseNavDrawer implements OnMapReadyCallback {

    ActivityHomeBinding mBinding;

    Boolean hasLocationPermissions;

    private SimpleLocation location;

    PermissionHelper permissionHelper;

    GoogleMap mMap;

    HomeViewModel model;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivityHomeBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        mBinding.mapView.onCreate(savedInstanceState);

        setListener();
    }

    void initBase() {

        model = new ViewModelProvider(this).get(HomeViewModel.class);

        model.setFireBaseToken();

//        model.getAuthLiveData().observe(Home.this, this::handleUserResponse);
    }

    private void setListener() {

        mBinding.menuIV.setOnClickListener(view -> openCloseDrawer());

        mBinding.includeLocationPopup.pickUpTV.setOnClickListener(view -> showSearchPlaceDialog());

        mBinding.includeLocationPopup.dropOffTV.setOnClickListener(view -> showSearchPlaceDialog());

        mBinding.includeLocationPopup.actionBT.setOnClickListener(view -> showCarPopUp());

        mBinding.includeCarPopup.includeCarView.getRoot().setOnClickListener(view -> showDriverFound());

        mBinding.includeFindDriverPopUp.cancelRideTV.setOnClickListener(view -> showCancelPopup());

        mBinding.includeFindDriverPopUp.textIV.setOnClickListener(view -> showMessageScreen());

        mBinding.includeCancelPopup.cancelTripTV.setOnClickListener(view -> cancelTrip());

        mBinding.includeCancelPopup.goBackTV.setOnClickListener(view -> goBack());

    }

    private void goBack() {

        mBinding.includeCancelPopup.getRoot().setVisibility(View.GONE);

        mBinding.includeFindDriverPopUp.getRoot().setVisibility(View.VISIBLE);

    }

    private void cancelTrip() {

        mBinding.includeLocationPopup.getRoot().setVisibility(View.VISIBLE);

        mBinding.includeCancelPopup.getRoot().setVisibility(View.GONE);

    }

    private void showMessageScreen() {

        startActivity(new Intent(Home.this, Chat.class));

    }

    private void showDriverFound() {

        mBinding.includeCarPopup.getRoot().setVisibility(View.GONE);

        mBinding.includeFindDriverPopUp.getRoot().setVisibility(View.VISIBLE);
    }


    private void showCancelPopup() {

        mBinding.includeFindDriverPopUp.getRoot().setVisibility(View.GONE);

        mBinding.includeCancelPopup.getRoot().setVisibility(View.VISIBLE);
    }

    private void showCarPopUp() {


        mBinding.includeLocationPopup.getRoot().setVisibility(View.GONE);

        mBinding.includeCarPopup.getRoot().setVisibility(View.VISIBLE);

    }

    private void showSearchPlaceDialog() {

        SearchPlaceDialog searchPlaceDialog = new SearchPlaceDialog(Home.this, model);

        searchPlaceDialog.show();

    }

    @Override
    protected void onResume() {

        super.onResume();

        mBinding.mapView.onResume();

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

        mBinding.mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {

        mMap = googleMap;

    }

    public static void startActivity(Activity c) {

        c.startActivity(new Intent(c, Home.class));

        c.finish();

    }

}
