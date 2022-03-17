package com.buzzware.apnigari.activities.home;

import static com.buzzware.apnigari.retrofit.Controller.Base_Url;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.buzzware.apnigari.R;
import com.buzzware.apnigari.activities.base.BaseNavDrawer;
import com.buzzware.apnigari.activities.home.dialog.SearchPlaceDialog;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.ExpandablePlacesListFragment;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.OnPredictedEvent;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.OnTextChangedEvent;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.geoCode.ReverseGeoCode;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.geoCode.ReverseGeoCodeResponse;
import com.buzzware.apnigari.activities.home.mo.VehicleModel;
import com.buzzware.apnigari.activities.home.vm.HomeViewModel;
import com.buzzware.apnigari.activities.messages.chat.Chat;
import com.buzzware.apnigari.commonModels.ride.RideModel;
import com.buzzware.apnigari.commonModels.ride.SearchedPlaceModel;
import com.buzzware.apnigari.databinding.ActivityHomeBinding;
import com.buzzware.apnigari.generic.GenericModelLiveData;
import com.buzzware.apnigari.retrofit.Controller;
import com.buzzware.apnigari.utils.AppConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.master.permissionhelper.PermissionHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


enum CurrentSelection {

    whereTo, currentLocation, secondDropOff

}

enum CurrentMode {

    activeRide, booking

}

public class Home extends BaseNavDrawer implements OnMapReadyCallback {

    ActivityHomeBinding mBinding;

    Boolean hasLocationPermissions;

    CurrentMode currentMode = CurrentMode.booking;

    CurrentSelection currentSelection = CurrentSelection.whereTo;

    private SimpleLocation location;

    PermissionHelper permissionHelper;

    GoogleMap mMap;

    HomeViewModel model;

    Boolean isSecondDropOffEnabled = false;

    SearchedPlaceModel placeWhereTo, placeCurrentLocation, placeSecondDropOff;

    Boolean isChangedFromMap = false;

    int distance = 0;

    int minutes = 0;

    public Marker locationMarker, driverMarker, destinationMarker;

//    RatingDialog ratingDialog;

    RideModel rideModel;

    String previousStatus;

    Polyline polyline;

    LatLng pastLatLng;

    VehicleModel vehicleDetails;

    Polyline secondDropOffPolyline;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivityHomeBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        mBinding.mapView.onCreate(savedInstanceState);

        setVisibilities();

        setListener();

        setFireBaseToken();
        
        setListeners();
    }

    private void setBottomSheet() {

        ExpandablePlacesListFragment expandablePlacesListFragment = new ExpandablePlacesListFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.bslContainer, expandablePlacesListFragment).addToBackStack("bslContainer").commit();

    }

    private void setVisibilities() {

        mBinding.secondDropOffLL.setVisibility(View.GONE);

    }


    private void setFireBaseToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.w("FireBase Token", "Fetching FCM registration token failed", task.getException());
                        return;

                    }

                    String token = task.getResult();

                    addTokenToDB(token);

                });

    }

    private void addTokenToDB(String token) {

        Map<String, Object> userData = new HashMap<>();

        userData.put("token", token);

        FirebaseFirestore.getInstance().collection("Users")
                .document(getUserId())
                .update(userData);
    }


    private void searchPlaceResponse(GenericModelLiveData data) {

        switch (data.status) {

            case error:
                break;
            case success:

                SearchedPlaceModel searchedPlaceModel = (SearchedPlaceModel) data.object;

                setLocationTexts(searchedPlaceModel);

                break;
            case loading:
                break;

        }

    }

    private void setLocationTexts(SearchedPlaceModel searchedPlaceModel) {

        if (currentSelection == CurrentSelection.currentLocation) {

            if (placeCurrentLocation == null) {

                currentSelection = CurrentSelection.whereTo;

            }

            placeCurrentLocation = searchedPlaceModel;

            mBinding.currentLocationET.setText(searchedPlaceModel.address);

        } else if (currentSelection == CurrentSelection.secondDropOff) {

            placeSecondDropOff = searchedPlaceModel;

            mBinding.destination2ET.setText(searchedPlaceModel.address);

        } else {

            placeWhereTo = searchedPlaceModel;

            mBinding.destinationET.setText(searchedPlaceModel.address);

        }

        setListeners();
    }

    private void setListener() {

        mBinding.menuIV.setOnClickListener(view -> openCloseDrawer());

//        mBinding.includeLocationPopup.pickUpTV.setOnClickListener(view -> showSearchPlaceDialog());

//        mBinding.includeLocationPopup.dropOffTV.setOnClickListener(view -> showSearchPlaceDialog());

//        mBinding.includeLocationPopup.actionBT.setOnClickListener(view -> showCarPopUp());

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

//        mBinding.includeLocationPopup.getRoot().setVisibility(View.VISIBLE);

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


//        mBinding.includeLocationPopup.getRoot().setVisibility(View.GONE);

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

                checkActiveRide();

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

    private void checkActiveRide() {

        model.getActiveRideLiveData().observe(Home.this, this::activeRideResponse);

        model.getActiveRide();


    }

    private void activeRideResponse(GenericModelLiveData data) {

//        switch ()

    }

    private void init() {

        mBinding.mapView.getMapAsync(this);

        model = new ViewModelProvider(this).get(HomeViewModel.class);

        model.setFireBaseToken();

        model.getSearchPlaceModelData().observe(Home.this, this::searchPlaceResponse);

    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {

        mMap = googleMap;

        disableLocationComponent();

        setMarkers();

        if (currentMode == CurrentMode.booking) {

            currentSelection = CurrentSelection.currentLocation;

            model.reverseGeoCode(location.getLatitude(), location.getLongitude());
        }

    }

    private void disableLocationComponent() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }


    private void setMarkers() {

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        float zoom = 18.0F;

        if (currentMode == CurrentMode.activeRide) {

            zoom = 8.0f;

        }


        // Enable GPS marker in Map
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoom));

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 500, null);

        mMap.setOnCameraMoveListener(() -> {

            if (currentMode == CurrentMode.booking) {

                hideKeyboard();

                mBinding.logoIV.setVisibility(View.VISIBLE);

                EventBus.getDefault().post(new ExpandablePlacesListFragment.HideBottomSheet());
            }
        });

        mMap.setOnCameraIdleListener(() -> {
            if (currentMode == CurrentMode.booking) {
                LatLng midLatLng = mMap.getCameraPosition().target;

                model.reverseGeoCode(midLatLng.latitude, midLatLng.longitude);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {

        mBinding.currentLocationET.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {

                mBinding.logoIV.setVisibility(View.GONE);

                EventBus.getDefault().post(new ExpandablePlacesListFragment.ShowBottomSheetMsg());

                currentSelection = CurrentSelection.currentLocation;

            }
        });
        mBinding.destinationET.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {

                mBinding.logoIV.setVisibility(View.GONE);

                EventBus.getDefault().post(new ExpandablePlacesListFragment.ShowBottomSheetMsg());

                currentSelection = CurrentSelection.whereTo;

            }
        });

        mBinding.destination2ET.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {

                EventBus.getDefault().post(new ExpandablePlacesListFragment.ShowBottomSheetMsg());

                currentSelection = CurrentSelection.secondDropOff;

            }
        });

        mBinding.addIV.setOnClickListener(v -> showSecondDropOff());

        mBinding.crossIV.setOnClickListener(v -> disableSecondDropOff());

        mBinding.currentLocationET.setOnTouchListener((v, event) -> {

            mBinding.logoIV.setVisibility(View.GONE);

            EventBus.getDefault().post(new ExpandablePlacesListFragment.ShowBottomSheetMsg());

            currentSelection = CurrentSelection.currentLocation;

            return false;

        });

        mBinding.destination2ET.setOnTouchListener((v, event) -> {

            EventBus.getDefault().post(new ExpandablePlacesListFragment.ShowBottomSheetMsg());

            currentSelection = CurrentSelection.secondDropOff;

            return false;

        });

        mBinding.mapView.setOnTouchListener((v, event) -> {

            isChangedFromMap = true;

            return false;
        });

        mBinding.destinationET.setOnTouchListener((v, event) -> {

            EventBus.getDefault().post(new ExpandablePlacesListFragment.ShowBottomSheetMsg());

            currentSelection = CurrentSelection.whereTo;

            return false;
        });

        mBinding.currentLocationET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!isChangedFromMap) {

                    currentSelection = CurrentSelection.currentLocation;

                    OnTextChangedEvent event = new OnTextChangedEvent();

                    event.data = s.toString();
                    event.latLng = location.getLatitude() + "%2C" + location.getLongitude();

                    EventBus.getDefault().post(event);
                }

                isChangedFromMap = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.destinationET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!isChangedFromMap) {

                    currentSelection = CurrentSelection.whereTo;

                    OnTextChangedEvent event = new OnTextChangedEvent();

                    event.latLng = location.getLatitude() + "%2C" + location.getLongitude();

                    event.data = s.toString();

                    EventBus.getDefault().post(event);
                }

                isChangedFromMap = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.destination2ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!isChangedFromMap) {

                    currentSelection = CurrentSelection.secondDropOff;

                    OnTextChangedEvent event = new OnTextChangedEvent();

                    event.latLng = location.getLatitude() + "%2C" + location.getLongitude();

                    event.data = s.toString();

                    EventBus.getDefault().post(event);
                }

                isChangedFromMap = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void disableSecondDropOff() {

        mBinding.secondDropOffLL.setVisibility(View.GONE);

        isSecondDropOffEnabled = false;

        if (currentSelection == CurrentSelection.secondDropOff) {

            currentSelection = CurrentSelection.whereTo;

        }

    }

    private void showSecondDropOff() {

        isSecondDropOffEnabled = true;

        mBinding.secondDropOffLL.setVisibility(View.VISIBLE);

    }



    public static void startActivity(Activity c) {

        c.startActivity(new Intent(c, Home.class));

        c.finish();

    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnPredictedEvent event) {

        hideKeyboard();

       model. reverseGeoCode(event.place.geometry.location.lat, event.place.geometry.location.lng);

    }

}
