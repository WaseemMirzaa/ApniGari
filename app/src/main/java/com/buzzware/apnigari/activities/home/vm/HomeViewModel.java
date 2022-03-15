package com.buzzware.apnigari.activities.home.vm;

import static com.buzzware.apnigari.activities.base.BaseActivity.getUserId;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.buzzware.apnigari.Firebase.FirebaseInstances;
import com.buzzware.apnigari.activities.auth.vm.mo.User;
import com.buzzware.apnigari.commonModels.ride.RideModel;
import com.buzzware.apnigari.generic.GenericModelLiveData;
import com.buzzware.apnigari.utils.AppConstants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<GenericModelLiveData> data, activeRideData;

    public LiveData<GenericModelLiveData> getAuthLiveData() {

        if (data == null) {

            data = new MutableLiveData<>();

        }

        return data;
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

}
