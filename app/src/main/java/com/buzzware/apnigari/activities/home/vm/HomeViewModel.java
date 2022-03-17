package com.buzzware.apnigari.activities.home.vm;

import static com.buzzware.apnigari.activities.base.BaseActivity.getUserId;
import static com.buzzware.apnigari.retrofit.Controller.Base_Url;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.buzzware.apnigari.Firebase.FirebaseInstances;
import com.buzzware.apnigari.activities.auth.vm.mo.User;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.geoCode.ReverseGeoCode;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.geoCode.ReverseGeoCodeResponse;
import com.buzzware.apnigari.commonModels.ride.RideModel;
import com.buzzware.apnigari.commonModels.ride.SearchedPlaceModel;
import com.buzzware.apnigari.generic.GenericModelLiveData;
import com.buzzware.apnigari.retrofit.Controller;
import com.buzzware.apnigari.utils.AppConstants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<GenericModelLiveData> data, activeRideData, searchPlaceModelData;

    public LiveData<GenericModelLiveData> getAuthLiveData() {

        if (data == null) {

            data = new MutableLiveData<>();

        }

        return data;
    }

    public LiveData<GenericModelLiveData> getSearchPlaceModelData() {

        if (searchPlaceModelData == null) {

            searchPlaceModelData = new MutableLiveData<>();

        }

        return searchPlaceModelData;
    }

    public LiveData<GenericModelLiveData> getActiveRideLiveData() {

        if (activeRideData == null) {

            activeRideData = new MutableLiveData<>();

        }

        return activeRideData;
    }

    public void getUserModel() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null)

            return;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseInstances.usersCollection.document(userId)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        User user = task.getResult().toObject(User.class);

                        if (user != null) {

                            user.id = task.getResult().getId();

                            data.postValue(new GenericModelLiveData(user, GenericModelLiveData.Status.success, null));

                        } else {

                            data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, "Unable to find user data. This user has been deleted by admin."));

                        }

                    } else {

                        if (task.getException() != null)

                            data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, task.getException().getLocalizedMessage()));

                    }

                });

    }


    public void getActiveRide() {

        activeRideData.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.loading, null));

        Query query = FirebaseFirestore.getInstance().collection("Bookings")
                .whereEqualTo("userId", getUserId())
                .whereIn("status", Arrays.asList(
                        "driverAccepted",
                        "driverReached",
                        "rideStarted",
                        "booked", "reBooked", AppConstants.RideStatus.CANCELLED,
                        AppConstants.RideStatus.RIDE_COMPLETED
                ));

        query.get()
                .addOnCompleteListener(
                        this::parseActiveRideSnapshot
                );
    }

    private void parseActiveRideSnapshot(Task<QuerySnapshot> task) {

        RideModel rideModel = null;

        if (task.getResult() != null) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                rideModel = document.toObject(RideModel.class);

                rideModel.id = document.getId();

                break;

            }
        }

        activeRideData.postValue(new GenericModelLiveData(rideModel, GenericModelLiveData.Status.success, null));

    }

    void parseBaseSnapshot(Task<QuerySnapshot> task) {

        RideModel rideModel = null;

        if (!task.isSuccessful()) {

            if (task.getException() != null)

                activeRideData.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, task.getException().getLocalizedMessage()));

            else

                activeRideData.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, "Unable to find Active Ride"));


            return;
        }

        if (task.getResult() != null) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                rideModel = document.toObject(RideModel.class);

                rideModel.id = document.getId();

                activeRideData.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.success, null));

                return;

            }

        }

        activeRideData.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, "No active ride found"));

    }


    public void setFireBaseToken() {

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

    Call<String> reverseCall;

    public void reverseGeoCode(double lat, double lng){

        String url = "/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi(Base_Url).getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    ReverseGeoCodeResponse reverseGeoCodeResponse = gson.fromJson(response.body(), ReverseGeoCodeResponse.class);

                    setLocationDetails(reverseGeoCodeResponse);

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                reverseCall = null;

            }
        });
    }

    private void setLocationDetails(ReverseGeoCodeResponse response) {

        SearchedPlaceModel searchedPlaceModel = new SearchedPlaceModel();

        List<ReverseGeoCode> reverseGeoCodeList = response.results;

        if (reverseGeoCodeList == null || reverseGeoCodeList.size() == 0)

            return;



        searchedPlaceModel.address = reverseGeoCodeList.get(0).formatted_address;
        searchedPlaceModel.lat = reverseGeoCodeList.get(0).geometry.location.lat;
        searchedPlaceModel.lng = reverseGeoCodeList.get(0).geometry.location.lng;
        searchedPlaceModel.status = "0";

        searchPlaceModelData.postValue(new GenericModelLiveData(searchedPlaceModel, GenericModelLiveData.Status.success, null));

    }

}
