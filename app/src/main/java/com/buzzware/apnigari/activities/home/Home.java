package com.buzzware.apnigari.activities.home;

import static com.buzzware.apnigari.retrofit.Controller.Base_Url;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.buzzware.apnigari.Firebase.FirebaseInstances;
import com.buzzware.apnigari.R;
import com.buzzware.apnigari.activities.auth.vm.mo.User;
import com.buzzware.apnigari.activities.base.BaseNavDrawer;
import com.buzzware.apnigari.activities.distance.DistanceActivity;
import com.buzzware.apnigari.activities.home.dialog.DialogRating;
import com.buzzware.apnigari.activities.home.dialog.SearchPlaceDialog;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.ExpandablePlacesListFragment;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.OnPredictedEvent;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.OnTextChangedEvent;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.directions.DirectionsApiResponse;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.directions.Leg;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.directions.Route;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.directions.Step;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.distanceMatrix.DistanceMatrixResponse;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.distanceMatrix.Element;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.distanceMatrix.Row;
import com.buzzware.apnigari.activities.home.mo.EmergencyModel;
import com.buzzware.apnigari.activities.home.mo.Price;
import com.buzzware.apnigari.activities.home.mo.Prices;
import com.buzzware.apnigari.activities.home.mo.VehicleModel;
import com.buzzware.apnigari.activities.home.vm.HomeViewModel;
import com.buzzware.apnigari.activities.messages.chat.Chat;
import com.buzzware.apnigari.activities.messages.chat.mo.ParcelableChat;
import com.buzzware.apnigari.commonModels.ride.RideModel;
import com.buzzware.apnigari.commonModels.ride.SearchedPlaceModel;
import com.buzzware.apnigari.commonModels.ride.TripDetail;
import com.buzzware.apnigari.databinding.ActivityHomeBinding;
import com.buzzware.apnigari.generic.GenericModelLiveData;
import com.buzzware.apnigari.retrofit.Controller;
import com.buzzware.apnigari.utils.AppConstants;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.master.permissionhelper.PermissionHelper;
import com.stripe.android.PaymentSession;
import com.stripe.android.Stripe;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

//    SearchedPlaceModel placeSecondDropOff;

    Boolean isChangedFromMap = false;

    double distance = 0;

    int minutes = 0;

    public Marker locationMarker, driverMarker, destinationMarker;

//    RatingDialog ratingDialog;

    RideModel rideModel;

    String previousStatus;

    Polyline polyline;

    LatLng pastLatLng;

    VehicleModel vehicleDetails;

    Polyline secondDropOffPolyline;

    SearchedPlaceModel pickUpLocation, destinationLocation, secondDropOff;

    Stripe stripe;

    PaymentSession paymentSession;

    Boolean readyToCharge = false;

    String clientSecret;
    String customerId;
    String orderClientSecret;
    List<String> nearByDrivers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivityHomeBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        mBinding.mapView.onCreate(savedInstanceState);

        setVisibilities();

        setListener();

        setListeners();
    }

    private void setBottomSheet() {

        ExpandablePlacesListFragment expandablePlacesListFragment = new ExpandablePlacesListFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.bslContainer, expandablePlacesListFragment).addToBackStack("bslContainer").commit();

    }

    private void setVisibilities() {

        mBinding.secondDropOffLL.setVisibility(View.GONE);

    }

    private void searchPlaceResponse(GenericModelLiveData data) {

        switch (data.status) {

            case error:
            case loading:
                break;
            case success:

                SearchedPlaceModel searchedPlaceModel = (SearchedPlaceModel) data.object;

                setLocationTexts(searchedPlaceModel);

                break;

        }

    }

    private void setLocationTexts(SearchedPlaceModel searchedPlaceModel) {

        if (currentSelection == CurrentSelection.currentLocation) {

            if (pickUpLocation == null) {

                currentSelection = CurrentSelection.whereTo;

            }

            pickUpLocation = searchedPlaceModel;

            mBinding.currentLocationET.setText(searchedPlaceModel.address);

        } else if (currentSelection == CurrentSelection.secondDropOff) {

            secondDropOff = searchedPlaceModel;

            mBinding.destination2ET.setText(searchedPlaceModel.address);

        } else {

            destinationLocation = searchedPlaceModel;

            mBinding.destinationET.setText(searchedPlaceModel.address);

        }

        setListeners();
    }

    private void setListener() {

        mBinding.menuIV.setOnClickListener(view -> openCloseDrawer());

        mBinding.actionTV.setOnClickListener(view -> validateLocationAndShowCarPopup());

        mBinding.includeCarPopup.includeCarView.getRoot().setOnClickListener(view -> showDriverFound());

        mBinding.includeFindDriverPopUp.cancelRideTV.setOnClickListener(view -> showCancelPopup());

        mBinding.includeCancelPopup.cancelTripTV.setOnClickListener(view -> cancelTrip());

        mBinding.includeCancelPopup.goBackTV.setOnClickListener(view -> goBack());

    }

    private void validateLocationAndShowCarPopup() {


        if (pickUpLocation == null || destinationLocation == null) {

            showErrorAlert("Please select destination first");

            return;
        }

        showCarPopUp();

    }

    private void goBack() {

        mBinding.includeCancelPopup.getRoot().setVisibility(View.GONE);

        mBinding.includeFindDriverPopUp.getRoot().setVisibility(View.VISIBLE);

    }

    private void cancelTrip() {

        mBinding.searchPlaceContainer.setVisibility(View.VISIBLE);
        mBinding.actionTV.setVisibility(View.VISIBLE);
        mBinding.logoIV.setVisibility(View.VISIBLE);
        mBinding.bslContainer.setVisibility(View.VISIBLE);

        mBinding.includeCancelPopup.getRoot().setVisibility(View.GONE);

    }

    private void showDriverFound() {

        mBinding.includeCarPopup.getRoot().setVisibility(View.GONE);
        mBinding.includeSearchingForDriver.getRoot().setVisibility(View.GONE);

        mBinding.includeFindDriverPopUp.getRoot().setVisibility(View.VISIBLE);
    }


    private void showCancelPopup() {

        mBinding.includeFindDriverPopUp.getRoot().setVisibility(View.GONE);

        mBinding.includeCancelPopup.getRoot().setVisibility(View.VISIBLE);
    }

    double min = 0;

    private void showCarPopUp() {

        distance = 0;

        min = 0;

        mBinding.actionTV.setVisibility(View.GONE);
        mBinding.bslContainer.setVisibility(View.GONE);
        mBinding.includeCarPopup.getRoot().setVisibility(View.VISIBLE);

        mBinding.includeCarPopup.pricingRG.setOnCheckedChangeListener((radioGroup, i) -> {

           onCheckChanged(i);

        });

        showLoader();

        getTotalDistanceTillPoint1();

        mMap.clear();

    }

    private void onCheckChanged(int i) {

        if (i == mBinding.includeCarPopup.iRideRB.getId()) {

            mBinding.includeCarPopup.includeCarView.priceTV.setText("Rs " + String.format("%.2f", iRidePrice));

            amount = iRidePrice;

        } else if (i == mBinding.includeCarPopup.plusRB.getId()) {

            mBinding.includeCarPopup.includeCarView.priceTV.setText("Rs " + String.format("%.2f", iRidePlusPrice));

            amount = iRidePlusPrice;

        } else if (i == mBinding.includeCarPopup.luxRB.getId()) {

            mBinding.includeCarPopup.includeCarView.priceTV.setText("Rs " + String.format("%.2f", iRideLuxPrice));

            amount = iRideLuxPrice;

        }
    }

    private void getTotalDistanceTillPoint1() {

        distance = 0;

        min = 0;

        showLoader();

        //todo destination also calculate for destination 2

        String url = "/maps/api/distancematrix/json?departure_time&origins=" + pickUpLocation.lat + "," + pickUpLocation.lng + "&destinations=" + destinationLocation.lat + "," + destinationLocation.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi(Base_Url).getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    DistanceMatrixResponse resp = gson.fromJson(response.body(), DistanceMatrixResponse.class);

                    setDistance(resp);

                    if (secondDropOff != null) {

                        getDistanceTillSecondDropOff();

                    } else {

                        getPrices();

                    }

                } else {

                    hideLoader();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
                hideLoader();
            }
        });
    }

    double iRideLuxPrice = 0;
    double iRidePlusPrice = 0;
    double iRidePrice = 0;
    double amount;

    private void getPrices() {

        FirebaseFirestore.getInstance().collection("Settings")
                .document("Prices")
                .get()
                .addOnCompleteListener(task -> {
                    hideLoader();
                    if (task.isSuccessful()) {

                        Prices settings = task.getResult().toObject(Prices.class);

                        distance = convertKmsToMiles(distance / 1000);

                        min = min / 60;

                        calculateLuxPrice(settings);

                        calculateIRidePrice(settings);

                        calculatePlusPrice(settings);

                        mBinding.includeCarPopup.includeCarView.priceTV.setText("$" + String.format("%.2f", iRidePrice));

                        mBinding.includeCarPopup.includeCarView.getRoot().setOnClickListener(view -> {

                            placeOrder();

                        });

                        amount = iRidePrice;

                        getDirections();

                    } else {

                        if (task.getException() != null && task.getException().getLocalizedMessage() != null)

                            showErrorAlert(task.getException().getLocalizedMessage());
                    }

                });

    }


    static String getAlphaNumericString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    private void placeOrder() {

        RideModel rideModel = new RideModel();

        rideModel.id = getAlphaNumericString(15);

        rideModel.bookingDate = new Date().getTime();

        rideModel.tripDetail = new TripDetail();

        rideModel.tripDetail.destinations = new ArrayList<>();

        rideModel.tripDetail.destinations.add(destinationLocation);

        if (secondDropOff != null) {

            rideModel.tripDetail.destinations.add(secondDropOff);

        }

        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(pickUpLocation.lat, pickUpLocation.lng));

        pickUpLocation.hash = hash;

        rideModel.tripDetail.pickUp = pickUpLocation;

        rideModel.userId = getUserId();

        rideModel.price = "" + amount;

        rideModel.status = "booked";

        getNearbyDrivers(rideModel);

    }

    void getNearbyDrivers(RideModel rideModel) {

        showLoader();

        FirebaseInstances
                .usersCollection
                .get()
                .addOnCompleteListener(task -> {

                    nearByDrivers = new ArrayList<>();

                    if(task.isSuccessful()) {

                        for(DocumentSnapshot documentSnapshot: task.getResult().getDocuments()) {

                            User user = documentSnapshot.toObject(User.class);

                            if (user != null) {

                                double distance = distance(user.lat, user.lng, pickUpLocation.lat, pickUpLocation.lng);

                                if (distance < 10 && user.token != null && !user.token.isEmpty() && (user.isActive != null && user.isActive) && user.isOnline != null && user.isOnline) {

                                    nearByDrivers.add(user.token);

                                }

                            }

                        }

                    }

                    hideLoader();

                    rideModel.fcmTokens = nearByDrivers;

                    FirebaseFirestore.getInstance().collection("Bookings")
                            .document(rideModel.id).set(rideModel);

                    Toast.makeText(this, "Successfully Booked", Toast.LENGTH_LONG).show();

                    checkActiveRide();
                });

    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    private void showSearchingForDriverPopup() {

        mBinding.includeSearchingForDriver.getRoot().setVisibility(View.VISIBLE);

        mBinding.includeCarPopup.getRoot().setVisibility(View.GONE);

        mBinding.logoIV.setVisibility(View.GONE);

        mBinding.searchPlaceContainer.setVisibility(View.GONE);

    }

    private void calculateLuxPrice(Prices settings) {

        Price price = settings.business;

        double total = price.getInitialFee() + (price.getPricePerMile() * distance) + (price.getPricePerMin() * min) + price.getCostOfVehicle();

        iRideLuxPrice = total;

    }

    private void calculateIRidePrice(Prices settings) {

        Price price = settings.go;

        double total = price.getInitialFee() + (price.getPricePerMile() * distance) + (price.getPricePerMin() * min) + price.getCostOfVehicle();

        iRidePrice = total;

    }

    private void calculatePlusPrice(Prices settings) {

        Price price = settings.goPlus;

        double total = price.getInitialFee() + (price.getPricePerMile() * distance) + (price.getPricePerMin() * min) + price.getCostOfVehicle();

        iRidePlusPrice = total;
    }


    public double convertKmsToMiles(double kms) {
        double miles = 0.621371 * kms;
        return miles;
    }

    private void getDistanceTillSecondDropOff() {

        //todo destination also calculate for destination 2

        String url = "/maps/api/distancematrix/json?departure_time&origins=" + destinationLocation.lat + "," + destinationLocation.lng + "&destinations=" + secondDropOff.lat + "," + secondDropOff.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi(Base_Url).getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    DistanceMatrixResponse resp = gson.fromJson(response.body(), DistanceMatrixResponse.class);

                    setDistance(resp);

                    getPrices();

                } else
                    hideLoader();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
                hideLoader();
            }
        });
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

        if (data.status == GenericModelLiveData.Status.success) {

            if (data.object == null) {

                setUpBookingView();

            } else {

                RideModel rideModel = (RideModel) data.object;

                setUpActiveRide(rideModel);

            }

        }

    }

    private void setUpActiveRide(RideModel rideModel) {

        currentMode = CurrentMode.activeRide;

        if (AppConstants.RideStatus.isRideInProgress(rideModel.status)) {

            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));

        }

        setEventListener(rideModel);
    }

    private void setUpBookingView() {

        mMap.clear();

        mBinding.includeSearchingForDriver.getRoot().setVisibility(View.GONE);
        mBinding.includeCarPopup.getRoot().setVisibility(View.GONE);
        mBinding.includeCancelPopup.getRoot().setVisibility(View.GONE);
        mBinding.includeFindDriverPopUp.getRoot().setVisibility(View.GONE);

        mBinding.searchPlaceContainer.setVisibility(View.VISIBLE);
        mBinding.actionTV.setVisibility(View.VISIBLE);
        mBinding.logoIV.setVisibility(View.VISIBLE);
        mBinding.bslContainer.setVisibility(View.VISIBLE);

        mBinding.locationLay.setVisibility(View.VISIBLE);

        mBinding.destination2ET.setEnabled(true);
        mBinding.destinationET.setEnabled(true);

        mBinding.vw.setVisibility(View.GONE);
        mBinding.vwDestinationET.setVisibility(View.GONE);
        mBinding.vwDestination2ET.setVisibility(View.GONE);

        mBinding.logoIV.setVisibility(View.VISIBLE);

        mBinding.currentLocationET.setEnabled(true);
        mBinding.emergencyIcon.setVisibility(View.INVISIBLE);
        currentMode = CurrentMode.booking;

        init();

        setListeners();

        ExpandablePlacesListFragment expandablePlacesListFragment = new ExpandablePlacesListFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.bslContainer, expandablePlacesListFragment).addToBackStack("bslContainer").commit();

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

        model.reverseGeoCode(event.place.geometry.location.lat, event.place.geometry.location.lng);

    }


    ListenerRegistration listenerRegistration;

    private void setEventListener(RideModel r) {

        FirebaseFirestore.getInstance().collection("Bookings")
                .document(r.id)
                .addSnapshotListener((value, error) -> {

                    if (value != null) {

                        RideModel rideModel = value.toObject(RideModel.class);
                        if (rideModel != null) {

                            rideModel.id = r.id;

                            mBinding.locationLay.setVisibility(View.VISIBLE);

                            mBinding.destination2ET.setEnabled(false);
                            mBinding.destinationET.setEnabled(false);
                            mBinding.currentLocationET.setEnabled(false);
                            mBinding.vw.setVisibility(View.VISIBLE);
                            mBinding.vwDestinationET.setVisibility(View.VISIBLE);
                            mBinding.vwDestination2ET.setVisibility(View.VISIBLE);

                            mBinding.vw.setOnClickListener(view -> startActivity(new Intent(Home.this, DistanceActivity.class)
                                    .putExtra("lat", rideModel.tripDetail.pickUp.lat)
                                    .putExtra("lng", rideModel.tripDetail.pickUp.lng)
                            ));

                            mBinding.vwDestinationET.setOnClickListener(view -> startActivity(new Intent(Home.this, DistanceActivity.class)
                                    .putExtra("lat", rideModel.tripDetail.destinations.get(0).lat)
                                    .putExtra("lng", rideModel.tripDetail.destinations.get(0).lng)
                            ));

                            mBinding.vwDestination2ET.setOnClickListener(view -> {

                                if (rideModel.tripDetail.destinations.size() == 2) {
                                    startActivity(new Intent(Home.this, DistanceActivity.class)
                                            .putExtra("lat", rideModel.tripDetail.destinations.get(1).lat)
                                            .putExtra("lng", rideModel.tripDetail.destinations.get(1).lng)
                                    );
                                }
                            });

                            if (rideModel.driverId != null) {

                                mBinding.emergencyIcon.setVisibility(View.VISIBLE);

                                mBinding.emergencyIcon.setOnClickListener(view -> sendEmergencyData(rideModel));

                                mBinding.emergencyIcon.setVisibility(View.VISIBLE);


                            }

                            //todo show from to destination texts

                            mBinding.logoIV.setVisibility(View.GONE);
                            mBinding.searchPlaceContainer.setVisibility(View.GONE);
                            mBinding.bslContainer.setVisibility(View.GONE);
                            mBinding.actionTV.setVisibility(View.GONE);

                            mBinding.destination2ET.setEnabled(false);
                            mBinding.destinationET.setEnabled(false);
                            mBinding.currentLocationET.setEnabled(false);
                            mBinding.vw.setVisibility(View.VISIBLE);
                            mBinding.vwDestinationET.setVisibility(View.VISIBLE);
                            mBinding.vwDestination2ET.setVisibility(View.VISIBLE);

                            mBinding.vw.setOnClickListener(view -> startActivity(new Intent(Home.this, DistanceActivity.class)
                                    .putExtra("lat", rideModel.tripDetail.pickUp.lat)
                                    .putExtra("lng", rideModel.tripDetail.pickUp.lng)
                            ));

                            mBinding.vwDestinationET.setOnClickListener(view -> startActivity(new Intent(Home.this, DistanceActivity.class)
                                    .putExtra("lat", rideModel.tripDetail.destinations.get(0).lat)
                                    .putExtra("lng", rideModel.tripDetail.destinations.get(0).lng)
                            ));

                            mBinding.vwDestination2ET.setOnClickListener(view -> {

                                if (rideModel.tripDetail.destinations.size() == 2) {
                                    startActivity(new Intent(Home.this, DistanceActivity.class)
                                            .putExtra("lat", rideModel.tripDetail.destinations.get(1).lat)
                                            .putExtra("lng", rideModel.tripDetail.destinations.get(1).lng)
                                    );
                                }
                            });

                            mBinding.currentLocationET.setText(rideModel.tripDetail.pickUp.address);
                            mBinding.destinationET.setText(rideModel.tripDetail.destinations.get(0).address);

                            mBinding.addIV.setVisibility(View.INVISIBLE);
                            mBinding.crossIV.setVisibility(View.INVISIBLE);

                            if (rideModel.tripDetail.destinations.size() > 1) {

                                mBinding.secondDropOffLL.setVisibility(View.VISIBLE);

                                mBinding.currentLocationET.setText(rideModel.tripDetail.destinations.get(1).address);

                                mBinding.addIV.setVisibility(View.INVISIBLE);
                            }

                            if (((previousStatus != null && previousStatus.equalsIgnoreCase(AppConstants.RideStatus.DRIVER_ACCEPTED)) && rideModel.status.equalsIgnoreCase(AppConstants.RideStatus.DRIVER_REACHED)) || (previousStatus == null && rideModel.status.equalsIgnoreCase(AppConstants.RideStatus.DRIVER_REACHED))) {

                                showErrorAlert("Your driver has arrived; Please make your way to the vehicle.");

                            }
                            previousStatus = rideModel.status;

                        }

                        if (this.rideModel != null) {

                            removePreviousPolyline(rideModel);

                        }

                        if (AppConstants.RideStatus.isRideInProgress(rideModel.status)) {

                            //todo handle commented
//                            mBinding.searchingForDrivers.setText("Have a nice trip");

                            //User is in car moving towards destination

                            showRideMarkers(rideModel);

                            //todo handle commented

//                            mBinding.onTripLL.setVisibility(View.VISIBLE);

//                            mBinding.clCard.setVisibility(View.GONE);

//                            mBinding.reachingLL.setVisibility(View.VISIBLE);

                        } else if (AppConstants.RideStatus.isRideDriverArriving(rideModel.status)) {

                            //Driver is Arriving

                            showRideMarkers(rideModel);

                            showDriverFound();

                            //todo handle commented

//                            mBinding.onTripLL.setVisibility(View.GONE);

//                            mBinding.reachingLL.setVisibility(View.VISIBLE);

                        } else if (AppConstants.RideStatus.BOOKED.equalsIgnoreCase(rideModel.status) || AppConstants.RideStatus.RE_BOOKED.equalsIgnoreCase(rideModel.status)) {

                            //Drive Booked But displaying Waiting for driver popup


                            if (driverMarker != null) {

                                driverMarker.remove();

                            }

                            if (polyline != null)

                                polyline.remove();

                            hideSecondPolyline();

                            if (listenerRegistration != null)

                                listenerRegistration.remove();

                            listenerRegistration = null;

                            this.rideModel = rideModel;

                            setWaitingForDriver(rideModel);

                        } else {

                            showRideMarkers(rideModel);

                        }
                    }

                });
    }


    void sendEmergencyData(RideModel rideModel) {

        showLoader();

        this.rideModel = rideModel;

        FirebaseInstances.usersCollection.document(getUserId())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        EmergencyModel emergencyModel = new EmergencyModel();

                        User user = task.getResult().toObject(User.class);

                        emergencyModel.userName = user.firstName + " " + user.lastName;
                        emergencyModel.userPhoneNumber = user.phoneNumber;
                        emergencyModel.userId = task.getResult().getId();

                        getDriverDetails(emergencyModel);

                    } else

                        hideLoader();

                });

    }


    private void getDriverDetails(EmergencyModel emergencyModel) {

        FirebaseInstances.usersCollection.document(rideModel.driverId)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        User user = task.getResult().toObject(User.class);

                        emergencyModel.driverId = task.getResult().getId();
                        emergencyModel.driverName = user.firstName + " " + user.lastName;
                        emergencyModel.lat = user.lat;
                        emergencyModel.lng = user.lng;

                        getVehicleDetails(
                                emergencyModel
                        );

                    }

                });
    }

    private void getVehicleDetails(EmergencyModel emergencyModel) {


        FirebaseFirestore.getInstance().collection("Vehicle")
                .document(rideModel.vehicleId)
                .get()
                .addOnCompleteListener(task -> {

//                    hideLoader();

                    if (task.isSuccessful()) {

                        vehicleDetails = task.getResult().toObject(VehicleModel.class);

                        emergencyModel.licenseUrl = vehicleDetails.getLicenseUrl();
                        emergencyModel.make = vehicleDetails.getMake();
                        emergencyModel.model = vehicleDetails.getModel();
                        emergencyModel.year = vehicleDetails.getYear();
                        emergencyModel.tagNumber = vehicleDetails.getTagNumber();
                        setEmergency(emergencyModel);
                    }

                });

    }

    private void setEmergency(EmergencyModel emergencyModel) {
        emergencyModel.adminStatus = "0";
        emergencyModel.soundStatus = "0";
        FirebaseInstances.emergencyCollection
                .document()
                .set(emergencyModel)
                .addOnCompleteListener(task -> {

                    hideLoader();

                    if (task.isSuccessful()) {

                        Toast.makeText(this, "Emergency Request sent", Toast.LENGTH_SHORT).show();

                    }

                });

    }


    @Override
    protected void onDestroy() {

        if (listenerRegistration != null)

            listenerRegistration.remove();

        listenerRegistration = null;

        super.onDestroy();

    }

    private void hideSecondPolyline() {

        if (secondDropOffPolyline != null) {

            secondDropOffPolyline.remove();

            secondDropOffPolyline = null;

        }
    }

    private void removePreviousPolyline(RideModel rideModel) {
        int size = rideModel.tripDetail.destinations.size();


        Boolean statusUpdated = false;

        if (size > 2) {

            String status1 = rideModel.tripDetail.destinations.get(0).status;
            String status2 = this.rideModel.tripDetail.destinations.get(0).status;

            statusUpdated = !status1.equalsIgnoreCase(status2);

        }

        if (!rideModel.status.equalsIgnoreCase(this.rideModel.status) || statusUpdated) {

            if (polyline != null)

                polyline.remove();

            if (secondDropOffPolyline != null)

                secondDropOffPolyline.remove();

            if (statusUpdated) {

                destinationMarker2.remove();
            }
        }
    }

    Marker bookedPickupMarker, bookedDestinationMarker, lastDestinationMarker, destinationMarker2;

    private void setWaitingForDriver(RideModel rideModel) {

        LatLng currentLatLng = new LatLng(rideModel.tripDetail.pickUp.lat, rideModel.tripDetail.pickUp.lng);

        bookedPickupMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title(""));

        LatLng dest = new LatLng(rideModel.tripDetail.destinations.get(0).lat, rideModel.tripDetail.destinations.get(0).lng);

        bookedDestinationMarker = mMap.addMarker(new MarkerOptions().position(dest).title(""));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));

        getDirections(rideModel.tripDetail.pickUp.lat, rideModel.tripDetail.pickUp.lng, rideModel.tripDetail.destinations.get(0).lat, rideModel.tripDetail.destinations.get(0).lng, false);

        if (rideModel.tripDetail.destinations.size() > 1) {

            LatLng secondDest = new LatLng(rideModel.tripDetail.destinations.get(1).lat, rideModel.tripDetail.destinations.get(1).lng);

            lastDestinationMarker = mMap.addMarker(new MarkerOptions().position(secondDest).title(""));

        }

        showSearchingForDriverPopup();

        mBinding.includeSearchingForDriver.cancelRideTV.setOnClickListener(v -> {

            FirebaseFirestore.getInstance().collection("Bookings")
                    .document(rideModel.id)
                    .update("status", AppConstants.RideStatus.DISPUTE);

            if (rideModel.driverId != null)

                FirebaseInstances.usersCollection.document(rideModel.driverId)
                        .update("isActive", true);

            FirebaseInstances.chatCollection.document(rideModel.id)
                    .delete().addOnCompleteListener(task -> {

                mMap.clear();

                checkActiveRide();

            });

        });
    }

    ArrayList<LatLng> path = new ArrayList<>();

    private void hideBookedMarkers() {

        //todo check how to deal with booked markers

        if (bookedPickupMarker != null) {

            bookedPickupMarker.remove();

            bookedPickupMarker = null;

        }

        if (bookedDestinationMarker != null) {

            bookedDestinationMarker.remove();

            bookedDestinationMarker = null;

        }

        if (destinationMarker2 != null)

            destinationMarker2.remove();

        destinationMarker2 = null;

        if (polyline != null)
            polyline.remove();
        if (secondDropOffPolyline != null)
            secondDropOffPolyline.remove();
    }


    private void showRideMarkers(RideModel ride) {

        rideModel = ride;

        hideBookedMarkers();

        if (rideModel.status.equalsIgnoreCase(AppConstants.RideStatus.RIDE_COMPLETED)) {

            setDriverListener();

            return;

        }


        mBinding.actionTV.setOnClickListener(v -> {

            FirebaseFirestore.getInstance().collection("Bookings")
                    .document(ride.id)
                    .update("status", AppConstants.RideStatus.DISPUTED);

            if (ride.driverId != null)

                FirebaseInstances.usersCollection.document(ride.driverId)
                        .update("isActive", true);

            FirebaseInstances.chatCollection.document(rideModel.id)
                    .delete();

            mMap.clear();

            checkActiveRide();

        });
        if (AppConstants.RideStatus.CANCELLED.equalsIgnoreCase(ride.status)) {

            if (ride.tripDetail.destinations.size() > 1) {

                //For multiple DropOff

                if (rideModel.tripDetail.destinations.get(0).status.equalsIgnoreCase(AppConstants.RideDetailStatus.NOT_REACHED)) {


                    LatLng currentLatLng = new LatLng(ride.tripDetail.destinations.get(0).lat, ride.tripDetail.destinations.get(0).lng);

                    destinationMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title(""));
                }

                LatLng destination2 = new LatLng(ride.tripDetail.destinations.get(1).lat, ride.tripDetail.destinations.get(1).lng);

                destinationMarker2 = mMap.addMarker(new MarkerOptions().position(destination2).title(""));

            } else {

                LatLng currentLatLng = new LatLng(ride.tripDetail.destinations.get(0).lat, ride.tripDetail.destinations.get(0).lng);

                destinationMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title(""));

            }

            showBookingCancelledAlert();

            return;

        } else if (AppConstants.RideStatus.isRideDriverArriving(ride.status)) {

            if (locationMarker != null) {

                //Driver is arriving showing driver location on map

                return;

            }

            //driver is arriving adding driver location marker to map

            LatLng currentLatLng = new LatLng(ride.tripDetail.pickUp.lat, ride.tripDetail.pickUp.lng);

            locationMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title(""));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));

            setDriverListener();

        } else if (AppConstants.RideStatus.isRideInProgress(ride.status)) {

            //ride is in progress show ride markers

            if (locationMarker != null) {

                locationMarker.remove();

                locationMarker = null;

            }

            setDriverListener();

            if (destinationMarker != null) {

                return;

            }

            if (ride.tripDetail.destinations.size() > 1) {

                //For multiple DropOff

                if (rideModel.tripDetail.destinations.get(0).status.equalsIgnoreCase(AppConstants.RideDetailStatus.NOT_REACHED)) {


                    LatLng currentLatLng = new LatLng(ride.tripDetail.destinations.get(0).lat, ride.tripDetail.destinations.get(0).lng);

                    destinationMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title(""));
                }

                LatLng destination2 = new LatLng(ride.tripDetail.destinations.get(1).lat, ride.tripDetail.destinations.get(1).lng);

                destinationMarker2 = mMap.addMarker(new MarkerOptions().position(destination2).title(""));

            } else {

                LatLng currentLatLng = new LatLng(ride.tripDetail.destinations.get(0).lat, ride.tripDetail.destinations.get(0).lng);

                destinationMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title(""));

            }


        }
    }

DialogRating ratingDialog;
    private void setDriverListener() {

        //For Driver Location Updates

        listenerRegistration = FirebaseFirestore.getInstance().collection("Users")
                .document(rideModel.driverId)
                .addSnapshotListener((value, error) -> {

                    if (value != null) {

                        User user = value.toObject(User.class);
                        user.id = value.getId();

                        if (rideModel.status.equalsIgnoreCase(AppConstants.RideStatus.RIDE_COMPLETED)) {

                            FirebaseInstances.chatCollection.document(rideModel.id)
                                    .delete();
                            //ride complete showing rating popup

//                            todo show rating popup
                            if (ratingDialog != null && ratingDialog.isShowing()) {
//
                                return;
                            }

                            mBinding.includeFindDriverPopUp.getRoot().setVisibility(View.GONE);
//
                            ratingDialog = new DialogRating(Home.this, user, rideModel);
//
                            ratingDialog.show();

                            return;

                        } else if (rideModel.status.equalsIgnoreCase(AppConstants.RideStatus.RATED)) {

                            listenerRegistration = null;

                            mMap.clear();

                            checkActiveRide();

                            return;
                        } else if (rideModel.status.equalsIgnoreCase(AppConstants.RideStatus.CANCELLED)) {

                            showBookingCancelledAlert();

                            return;

                        }
                        //Update Driver Location On Map
                        updateDriverLocation(user);

                        mBinding.includeFindDriverPopUp.textIV.setOnClickListener(view -> startChat((user)));

                        setUserData(user);

                        setPhoneListener(user);

                        if (vehicleDetails == null) {

                            getVehicleDetails();

                        }

                    }

                });

    }


    private void getVehicleDetails() {

        showLoader();

        FirebaseFirestore.getInstance().collection("Vehicle")
                .document(rideModel.vehicleId)
                .get()
                .addOnCompleteListener(task -> {

                    hideLoader();

                    if (task.isSuccessful()) {

                        vehicleDetails = task.getResult().toObject(VehicleModel.class);

                        if (vehicleDetails != null) {

                            setVehicleData();

                        }

                    }

                });


    }

    private void setVehicleData() {

        if (!Home.this.isDestroyed()) {

            mBinding.includeFindDriverPopUp.carNoTV.setText(vehicleDetails.getTagNumber());

            mBinding.includeFindDriverPopUp.carNameTV.setText(vehicleDetails.getName() + " " + vehicleDetails.getMake());
//
//            mBinding.colorTV.setText(vehicleDetails.getColor());
//
//            mBinding.modelTV.setText(vehicleDetails.getModel());
//
//
//            Glide.with(BookARideActivity.this).load(vehicleDetails.frontCarUrl).apply(new RequestOptions().centerCrop()).into(mBinding.carPic);

        }
    }


    private void setPhoneListener(User user) {

        mBinding.includeFindDriverPopUp.callIV.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + user.phoneNumber));
            startActivity(intent);

        });
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {

        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        vectorDrawable.setBounds(20, 20, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private void updateDriverLocation(User user) {

        LatLng currentLatLng = new LatLng(user.lat, user.lng);

        //if already showing marker then move marker else add driver marker

        if (driverMarker == null)

            driverMarker = mMap
                    .addMarker(
                            new MarkerOptions()
                                    .position(currentLatLng)
                                    .title("")
                                    .icon(
                                            BitmapFromVector(
                                                    Home.this, R.drawable.moveable_car)
                                    )
                    );

        else {

            if (pastLatLng == null) {

                pastLatLng = currentLatLng;

            }

            //moving driver marker

            driverMarker.setPosition(currentLatLng);


            double fLat = (Math.PI * pastLatLng.latitude) / 180.0f;
            double fLng = (Math.PI * pastLatLng.longitude) / 180.0f;
            double tLat = (Math.PI * currentLatLng.latitude) / 180.0f;
            double tLng = (Math.PI * currentLatLng.longitude) / 180.0f;

            double degree = radiansToDegrees(Math.atan2(sin(tLng - fLng) * cos(tLat), cos(fLat) * sin(tLat) - sin(fLat) * cos(tLat) * cos(tLng - fLng)));

            double bearing = 0;

            if (degree >= 0) {

                bearing = degree;

            } else {

                bearing = 360 + degree;

            }

            driverMarker.setRotation((float) bearing);

            pastLatLng = currentLatLng;

        }

        if (AppConstants.RideStatus.isRideDriverArriving(rideModel.status)) {


            //Rider is arriving show path from driver to pickup location

//            if (polyline != null) {

//                polyline.remove();

//            }

            getDirections(user.lat, user.lng, rideModel.tripDetail.pickUp.lat, rideModel.tripDetail.pickUp.lng, false);


        } else {

            if (rideModel.tripDetail.destinations.size() == 1) {

                //Either or ride is active and signle dropoff

                getDirections(user.lat, user.lng, rideModel.tripDetail.destinations.get(0).lat, rideModel.tripDetail.destinations.get(0).lng, false);

            } else if (rideModel.tripDetail.destinations.size() > 1) {


                if (AppConstants.RideDetailStatus.hasReached(rideModel.tripDetail.destinations.get(0).status)) {

                    //rider is moving towards destination 2

                    getDirections(user.lat, user.lng, rideModel.tripDetail.destinations.get(1).lat, rideModel.tripDetail.destinations.get(1).lng, true);


                } else {

                    //rider is moving towards destination 1 show path from destination 1 to 2

                    getDirections(user.lat, user.lng, rideModel.tripDetail.destinations.get(0).lat, rideModel.tripDetail.destinations.get(0).lng, false);

                }
            }
        }

    }

    private double radiansToDegrees(double x) {
        return x * 180.0 / Math.PI;
    }


    private void setUserData(User user) {

        mBinding.includeFindDriverPopUp.nameTV.setText(user.firstName + " " + user.lastName);

        if (!Home.this.isFinishing())
            Glide.with(Home.this)
                    .load(user.image)
                    .into(mBinding.includeFindDriverPopUp.picIV);

        double rating = 0;

        if (user.ratings != null) {

            for (Double r : user.ratings)

                rating = rating + r;

        }

        if (rating > 0) {

            rating = rating / user.ratings.size();

            mBinding.includeFindDriverPopUp.ratingBar.setRating(Double.valueOf(rating).floatValue());

        }

        mBinding.includeFindDriverPopUp.callIV.setOnClickListener(v -> startChat(user));

    }

    private void startChat(User user) {

        Intent intent = new Intent(Home.this, Chat.class);

        ParcelableChat parcelableChat = new ParcelableChat();
        parcelableChat.setConversationID(rideModel.id);
        parcelableChat.setSelectedUserId(user.id);
        parcelableChat.setSelectedUserName(user.firstName + " " + user.lastName);
        parcelableChat.setTypeStatus("true");

        intent.putExtra("parcelableChat", parcelableChat);

        startActivity(intent);

    }

    AlertDialog alertDialog;

    public void showBookingCancelledAlert() {

        if (alertDialog != null && alertDialog.isShowing())

            alertDialog.dismiss();

        alertDialog = new AlertDialog.Builder(Home.this)
                .setMessage("Sorry, no driver was found at the moment. This ride was canceled. Please try again.")
                .setTitle("Alert")
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> {

                    dialog.dismiss();

                    rideModel.status = AppConstants.RideStatus.DISPUTED;

                    FirebaseInstances.bookingsCollection
                            .document(rideModel.id).set(rideModel);

//                    finish();

                    mMap.clear();

                    checkActiveRide();

                })
                .create();

        alertDialog.show();
    }


    Boolean calledForSecondDropOff = false;

    void getDirections() {

        String url = null;

        if (!calledForSecondDropOff) {

            url = "/maps/api/directions/json?origin=" + pickUpLocation.lat + "," + pickUpLocation.lng + "&destination=" + destinationLocation.lat + "," + destinationLocation.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        } else {

            url = "/maps/api/directions/json?origin=" + destinationLocation.lat + "," + destinationLocation.lng + "&destination=" + secondDropOff.lat + "," + secondDropOff.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        }

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi(Base_Url).getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    DirectionsApiResponse resp = gson.fromJson(response.body(), DirectionsApiResponse.class);

                    drawPaths(resp);

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }


    private void drawPaths(DirectionsApiResponse res) {

        ArrayList<LatLng> path = new ArrayList<>();

        try {

            if (res.routes != null && res.routes.size() > 0) {

                Route route = res.routes.get(0);

                if (route.legs != null) {

                    for (int i = 0; i < route.legs.size(); i++) {

                        Leg leg = route.legs.get(i);

                        if (leg.steps != null) {

                            for (int j = 0; j < leg.steps.size(); j++) {

                                Step step1 = leg.steps.get(j);

                                if (step1.polyline != null) {

                                    List<LatLng> decoded = PolyUtil.decode(step1.polyline.points);

                                    path.addAll(decoded);

                                }

                            }

                        }

                    }

                }

            }

        } catch (Exception ex) {

        }

        //Draw the polyline
        if (path.size() > 0) {

            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLACK).width(10);

            mMap.addPolyline(opts);

        }

        if (!calledForSecondDropOff) {

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(pickUpLocation.lat, pickUpLocation.lng))
                    .title("Pickup Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(destinationLocation.lat, destinationLocation.lng))
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            if (secondDropOff != null) {

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(secondDropOff.lat, secondDropOff.lng))
                        .title("Destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                calledForSecondDropOff = true;
                getDirections();

            }
        }
    }

    void getDirections(double originLat, double originLng, double destinationLat, double destinationLng, Boolean isSecondDropOff) {

        String url = "/maps/api/directions/json?origin=" + originLat + "," + originLng + "&destination=" + destinationLat + "," + destinationLng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi(Base_Url).getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    DirectionsApiResponse resp = gson.fromJson(response.body(), DirectionsApiResponse.class);

                    drawPaths(resp, isSecondDropOff);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }


    private void drawPaths(DirectionsApiResponse res, Boolean isSecondDropOff) {
        path = new ArrayList<>();

//        ArrayList<LatLng> path = new ArrayList<>();

        try {

            if (res.routes != null && res.routes.size() > 0) {

                Route route = res.routes.get(0);

                if (route.legs != null) {

                    for (int i = 0; i < route.legs.size(); i++) {

                        Leg leg = route.legs.get(i);

                        if (leg.steps != null) {

                            for (int j = 0; j < leg.steps.size(); j++) {

                                Step step1 = leg.steps.get(j);

                                if (step1.polyline != null) {

                                    List<LatLng> decoded = PolyUtil.decode(step1.polyline.points);

                                    path.addAll(decoded);

                                }

                            }

                        }

                    }

                }

            }

        } catch (Exception ex) {

            ex.printStackTrace();

        }

        //Draw the polyline
        if (path.size() > 0) {

            if (secondDropOffPolyline != null)

                secondDropOffPolyline.remove();

            secondDropOffPolyline = null;

            if (AppConstants.RideStatus.isRideDriverArriving(rideModel.status)) {


                //Rider is arriving show path from driver to pickup location

                if (polyline != null) {

                    polyline.remove();

                }
            }
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLACK).width(10);

            polyline = mMap.addPolyline(opts);

        }

//        if (AppConstants.RideStatus.isRideInProgress(rideModel.status)) {

        calculateDistance();

//        }
    }

    Call<String> reverseCall;

    void calculateDistance() {

        //todo destination also calculate for destination 2

        String url = "/maps/api/distancematrix/json?departure_time&origins=" + location.getLatitude() + "," + location.getLongitude() + "&destinations=" + rideModel.tripDetail.destinations.get(0).lat + "," + rideModel.tripDetail.destinations.get(0).lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi(Base_Url).getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    DistanceMatrixResponse resp = gson.fromJson(response.body(), DistanceMatrixResponse.class);

                    setDistance(resp);

                    checkPolyline2();

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }

    private void setDistance(DistanceMatrixResponse resp) {

        String distance = "";
        String time = "";
        String currentAddress = "";

        if (resp.origin_addresses != null && resp.origin_addresses.size() > 0) {

            currentAddress = resp.origin_addresses.get(0);

        }

        if (resp.rows != null && resp.rows.size() > 0) {

            Row row = resp.rows.get(0);

            if (row.elements != null && row.elements.size() > 0) {

                Element element = row.elements.get(0);

                if (element.distance != null) {

                    distance = element.distance.text;
                    this.distance = element.distance.value;

                }

                if (element.duration != null) {

                    time = element.duration.text;
                    minutes = element.duration.value;

                }

            }

        }


        mBinding.includeFindDriverPopUp.timeTV.setText(time + "     " +  distance + "");

    }

    Call<String> reverseCall1;

    void getDirectionsTowardsDropOff2(double originLat, double originLng, double destinationLat, double destinationLng, Boolean isSecondDropOff) {

        String url = "/maps/api/directions/json?origin=" + originLat + "," + originLng + "&destination=" + destinationLat + "," + destinationLng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall1 != null) {

            reverseCall1.cancel();

            reverseCall1 = null;
        }

        reverseCall1 = Controller.getApi(Base_Url).getPlaces(url, "asdasd");

        reverseCall1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                reverseCall1 = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    DirectionsApiResponse resp = gson.fromJson(response.body(), DirectionsApiResponse.class);

                    try {

                        drawPaths2(resp, isSecondDropOff);

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                    calculateDistance2();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }

    void calculateDistance2() {
        SearchedPlaceModel pickUp = rideModel.tripDetail.destinations.get(0);
        SearchedPlaceModel destination1 = rideModel.tripDetail.destinations.get(0);
        SearchedPlaceModel destination2 = rideModel.tripDetail.destinations.get(1);

        //todo destination also calculate for destination 2

        String url = "/maps/api/distancematrix/json?departure_time&origins=" + destination1.lat + "," + destination1.lng + "&destinations=" + destination2.lat + "," + destination2.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi(Base_Url).getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    DistanceMatrixResponse resp = gson.fromJson(response.body(), DistanceMatrixResponse.class);

                    setDistance2(resp);

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }


    private void setDistance2(DistanceMatrixResponse resp) {

        String distance = "";
        String time = "";
        String currentAddress = "";

        if (resp.origin_addresses != null && resp.origin_addresses.size() > 0) {

            currentAddress = resp.origin_addresses.get(0);

        }

        if (resp.rows != null && resp.rows.size() > 0) {

            Row row = resp.rows.get(0);

            if (row.elements != null && row.elements.size() > 0) {

                Element element = row.elements.get(0);

                if (element.distance != null) {

                    this.distance = this.distance + element.distance.value;

                    distance = element.distance.text;

                }

                if (element.duration != null) {

                    minutes = element.duration.value + minutes;

                    time = element.duration.text;

                }

            }

        }


        float min = minutes / (60);
        double dis = this.distance / 1000;

        mBinding.includeFindDriverPopUp.timeTV.setText(String.format("%.2f", min) + " minutes     " + String.format("%.2f", dis) + " km");

    }


    private void checkPolyline2() {

        if (AppConstants.RideStatus.BOOKED.equalsIgnoreCase(rideModel.status) || AppConstants.RideStatus.RE_BOOKED.equalsIgnoreCase(rideModel.status)) {

            //draw polyline from dest1 to second drop off 2
            drawSecondPolyline(rideModel);

        } else if (AppConstants.RideStatus.RIDE_STARTED.equalsIgnoreCase(rideModel.status) && rideModel.tripDetail.destinations.get(0).status.equalsIgnoreCase(AppConstants.RideDetailStatus.NOT_REACHED)) {

            //ride is started handle case for driver hasn't reached any location yet draw polyline draw from dest 1 to dest 2
            //drawing polyline from dest1 to second drop off 2

            drawSecondPolyline(rideModel);

        } else {

            hideSecondPolyline();

        }

    }

    private void drawSecondPolyline(RideModel rideModel) {

        SearchedPlaceModel pickUp = rideModel.tripDetail.destinations.get(0);

        if (rideModel.tripDetail.destinations.size() == 2) {

            SearchedPlaceModel destination = rideModel.tripDetail.destinations.get(1);

            getDirectionsTowardsDropOff2(pickUp.lat, pickUp.lng, destination.lat, destination.lng, false);
        }
    }


    private void drawPaths2(DirectionsApiResponse res, Boolean isSecondDropOff) {

//        ArrayList<LatLng> path = new ArrayList<>();

        try {

            if (res.routes != null && res.routes.size() > 0) {

                Route route = res.routes.get(0);

                if (route.legs != null) {

                    for (int i = 0; i < route.legs.size(); i++) {

                        Leg leg = route.legs.get(i);

                        if (leg.steps != null) {

                            for (int j = 0; j < leg.steps.size(); j++) {

                                Step step1 = leg.steps.get(j);

                                if (step1.polyline != null) {

                                    List<LatLng> decoded = PolyUtil.decode(step1.polyline.points);

                                    path.addAll(decoded);

                                }

                            }

                        }

                    }

                }

            }

        } catch (Exception ex) {

            ex.printStackTrace();

        }

        //Draw the polyline
        if (path.size() > 0) {

            if (polyline != null)

                polyline.remove();

            polyline = null;

            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLACK).width(10);

            polyline = mMap.addPolyline(opts);

        }

    }


}
