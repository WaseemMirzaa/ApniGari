package com.buzzware.apnigari.activities.home.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.apnigari.activities.auth.vm.mo.User;
import com.buzzware.apnigari.activities.home.dialog.adapter.SearchPlaceAdapter;
import com.buzzware.apnigari.activities.home.vm.HomeViewModel;
import com.buzzware.apnigari.commonModels.ride.RideModel;
import com.buzzware.apnigari.databinding.DialogRatingBinding;
import com.buzzware.apnigari.databinding.DialogSearchLocationBinding;
import com.buzzware.apnigari.utils.AppConstants;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DialogRating extends Dialog {

    DialogRatingBinding binding;

    public DialogRating(@NonNull Context context, User driver, RideModel rideModel) {


        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = DialogRatingBinding.inflate(LayoutInflater.from(context));

        setContentView(binding.getRoot());

        Glide.with(context).load(driver.image).apply(new RequestOptions().centerCrop()).into(binding.picIV);

        binding.nameTV.setText(driver.firstName + " " + driver.lastName);

        double rating = 0;

        if (driver.ratings != null) {

            for (double r : driver.ratings) {

                rating = rating + r;

            }

            rating = rating / Double.valueOf(driver.ratings.size());
        }

        binding.currentRatingRB.setRating(new Double(rating).floatValue());

        binding.submitReviewTV.setOnClickListener(view -> submitRating(driver,rideModel));
        binding.closeIV.setOnClickListener(view -> submitRating(rideModel));
        setFullScreen();


    }

    private void submitRating(RideModel rideModel) {

        FirebaseFirestore.getInstance().collection("Bookings").document(rideModel.id)
                .update("status", AppConstants.RideStatus.RATED);

        dismiss();

    }

    private void submitRating(User user, RideModel ride) {

        if (user.ratings == null) {

            user.ratings = new ArrayList<>();

        }


        if (user.reviews == null) {

            user.reviews = new ArrayList<>();

        }

        user.ratings.add(Float.valueOf(binding.ratingRB.getRating()).doubleValue());
        user.reviews.add(binding.commentsET.getText().toString());

        FirebaseFirestore.getInstance().collection("Bookings").document(ride.id)
                .update("status", AppConstants.RideStatus.RATED);

        FirebaseFirestore.getInstance().collection("Users").document(ride.driverId)
                .update("ratings", user.ratings);

        FirebaseFirestore.getInstance().collection("Users").document(ride.driverId)
                .update("reviews", user.reviews);


        dismiss();

    }
    private void setFullScreen() {

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;

        window.setAttributes(wlp);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

}
