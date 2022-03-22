package com.buzzware.apnigari.activities.distance;

import static com.buzzware.apnigari.retrofit.Controller.Base_Url;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.buzzware.apnigari.activities.base.BaseActivity;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.directions.DirectionsApiResponse;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.directions.Leg;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.directions.Route;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.directions.Step;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.distanceMatrix.DistanceMatrixResponse;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.distanceMatrix.Element;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.distanceMatrix.Row;
import com.buzzware.apnigari.databinding.ActivityDistanceBinding;
import com.buzzware.apnigari.retrofit.Controller;
import com.buzzware.apnigari.utils.AppConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.jetbrains.annotations.NotNull;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DistanceActivity extends BaseActivity implements OnMapReadyCallback {

    ActivityDistanceBinding mBinding;

    Context context;

    public GoogleMap mMap;

    Polyline polyline;

    private SimpleLocation location;

    LatLng pastLatLng;

    Boolean hasLocationPermissions = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivityDistanceBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        getExtrasFromIntent();

        mBinding.homeMapView.onCreate(savedInstanceState);
    }

    private void getExtrasFromIntent() {

        double lat = getIntent().getExtras().getDouble("lat");
        double lng = getIntent().getExtras().getDouble("lng");

        pastLatLng = new LatLng(lat, lng);

    }

    @Override
    public void onResume() {
        super.onResume();

        checkPermissionsAndInit();

    }

    private void checkPermissionsAndInit() {

        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        Permissions.check(this/*context*/, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {

                hasLocationPermissions = true;

                location = new SimpleLocation(DistanceActivity.this);

                if (!location.hasLocationEnabled()) {
                    // ask the user to enable location access
                    showEnableLocationDialog("Please enable location from setting in order to proceed to the app");

                    return;
                }

                location.beginUpdates();

                init();

                setListeners();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {

                hasLocationPermissions = false;

                showPermissionsDeniedError("Please enable location permissions from setting in order to proceed to the app.");
            }
        });
    }

    @Override
    public void onPause() {

        disableLocationUpdates();

        super.onPause();
    }

    private void disableLocationUpdates() {

        try {

            location.endUpdates();

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void setListeners() {

        mBinding.include.backIV.setOnClickListener(v -> finish());

    }

    private void init() {

        context = DistanceActivity.this;

        mBinding.homeMapView.onResume();

        mBinding.homeMapView.getMapAsync(this);
    }


    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.addMarker(new MarkerOptions().position(pastLatLng).title(""));
        mMap.addMarker(new MarkerOptions().position(current).title(""));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 18.0F));

        getDirections(pastLatLng.latitude, pastLatLng.longitude, current.latitude, current.longitude, false);

    }


    Call<String> reverseCall;

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

                    drawPaths(resp);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }

    ArrayList<LatLng> path = new ArrayList<>();

    private void drawPaths(DirectionsApiResponse res) {
        path = new ArrayList<>();

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

            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLACK).width(10);

            polyline = mMap.addPolyline(opts);

        }

        calculateDistance();

    }



    int distance = 0;
    int minutes = 0;

    void calculateDistance() {

        //todo destination also calculate for destination 2

        String url = "/maps/api/distancematrix/json?departure_time&origins=" + location.getLatitude() + "," + location.getLongitude() + "&destinations=" + pastLatLng.latitude + "," + pastLatLng.longitude + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

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


        mBinding.timeTV.setText(time);
        mBinding.kmTV.setText(distance);
    }

}
