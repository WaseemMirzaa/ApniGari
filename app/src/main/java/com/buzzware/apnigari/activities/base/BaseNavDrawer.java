package com.buzzware.apnigari.activities.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.apnigari.R;
import com.buzzware.apnigari.activities.auth.Login;
import com.buzzware.apnigari.activities.auth.vm.mo.User;
import com.buzzware.apnigari.activities.base.vm.BaseViewModel;
import com.buzzware.apnigari.activities.carBookings.CarBookings;
import com.buzzware.apnigari.activities.home.Home;
import com.buzzware.apnigari.activities.messages.chatList.ChatList;
import com.buzzware.apnigari.activities.refer.Refer;
import com.buzzware.apnigari.activities.rentACar.RentACar;
import com.buzzware.apnigari.activities.setting.SettingActivity;
import com.buzzware.apnigari.activities.support.Support;
import com.buzzware.apnigari.activities.wallet.Wallet;
import com.buzzware.apnigari.activities.yourTrip.YourTrip;
import com.buzzware.apnigari.databinding.AppBaseLayoutBinding;
import com.buzzware.apnigari.generic.GenericModelLiveData;

public class BaseNavDrawer extends BaseActivity implements View.OnClickListener {

    AppBaseLayoutBinding binding;

    BaseViewModel baseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = AppBaseLayoutBinding.inflate(getLayoutInflater());

        super.setContentView(binding.getRoot());// The base layout that contains your navigation drawer.

        initBase();

        setBaseListeners();

    }

    void initBase() {

        baseModel = new ViewModelProvider(this).get(BaseViewModel.class);

        baseModel.getAuthLiveData().observe(BaseNavDrawer.this, this::handleUserResponse);

        baseModel.getUserModel();

        baseModel.getActiveRideLiveData().observe(BaseNavDrawer.this, this::handleActiveRideResponse);

//        baseModel.getActiveRide();
    }

    private void handleActiveRideResponse(GenericModelLiveData genericModelLiveData) {


        switch (genericModelLiveData.status) {

            case error:

                hideLoader();

                openCloseDrawer();

                showErrorAlert(genericModelLiveData.errorMsg);

                break;

            case loading:

                showLoader();

                break;

            case success:

                hideLoader();

//                startActivity(new Intent(BaseNavDrawer.this, Home.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
//
//                finish();

                break;
        }
    }

    private void handleUserResponse(GenericModelLiveData genericModelLiveData) {

        switch (genericModelLiveData.status) {

            case error:

//                hideLoader();
//
//                showErrorAlert(genericModelLiveData.errorMsg);

                break;

            case loading:

                showLoader();

                break;

            case success:

                hideLoader();

                User user = (User) genericModelLiveData.object;

                setUserData(user);

                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setUserData(User user) {

        if (user.firstName != null && user.lastName != null)

            binding.navView.userNameTV.setText(user.firstName + " " + user.lastName);

        if (user.image != null)

            Glide.with(this)
                    .load(user.image)
                    .apply(
                            new RequestOptions()
                                    .centerCrop()
                                    .placeholder(R.drawable.logo_black)
                    )
                    .into(binding.navView.userIV);
    }


    protected void setBaseListeners() {

        binding.navView.rentACarLL.setOnClickListener(view -> {

            openCloseDrawer();

            startActivity(new Intent(BaseNavDrawer.this, RentACar.class));

            finish();
        });

        binding.navView.homeLL.setOnClickListener(view -> {

            openCloseDrawer();

            startActivity(new Intent(BaseNavDrawer.this, Home.class));

            finish();
        });

        binding.navView.logoutLL.setOnClickListener(view -> {

            openCloseDrawer();

            logout();
        });

        binding.navView.bookingLL.setOnClickListener(view -> {

            openCloseDrawer();

            startActivity(new Intent(BaseNavDrawer.this, CarBookings.class));

            finish();

        });

        binding.navView.messagesLL.setOnClickListener(view -> {

            openCloseDrawer();

            startActivity(new Intent(BaseNavDrawer.this, ChatList.class));

            finish();
        });

        binding.navView.yourTripLL.setOnClickListener(view -> {

            openCloseDrawer();

            startActivity(new Intent(BaseNavDrawer.this, YourTrip.class));

            finish();
        });

        binding.navView.walletLL.setOnClickListener(view -> {

            openCloseDrawer();

            startActivity(new Intent(BaseNavDrawer.this, Wallet.class));

            finish();
        });

        binding.navView.supportLL.setOnClickListener(view -> {

            openCloseDrawer();

            startActivity(new Intent(BaseNavDrawer.this, Support.class));

        });


        binding.navView.referLL.setOnClickListener(view -> {

            openCloseDrawer();

            startActivity(new Intent(BaseNavDrawer.this, Refer.class));

            finish();
        });

        binding.navView.settingsLL.setOnClickListener(view -> {

            openCloseDrawer();

            startActivity(new Intent(BaseNavDrawer.this, SettingActivity.class));

            finish();
        });
    }

    private void logout() {

        startActivity(new Intent(BaseNavDrawer.this, Login.class)

                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

        finish();

    }

    @Override
    public void setContentView(int layoutResID) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        View stubView = inflater.inflate(layoutResID, binding.containerFL, false);

        binding.containerFL.addView(stubView, lp);
    }

    @Override
    public void setContentView(View view) {

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        binding.containerFL.addView(view, lp);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {

        binding.containerFL.addView(view, params);

    }


    @Override
    public void onClick(View v) {
//        if (v == binding.navView.findViewById(R.id.homeLay)) {
//
//            showLoader();
//            getActiveRide();
//
//
//        } else if (v == binding.navView.findViewById(R.id.bookingsLay)) {
//
//            openCloseDrawer();
//
//            startActivity(new Intent(BaseNavDrawer.this, BookingsActivity.class));
//            finish();
//
//        } else if (v == binding.navView.findViewById(R.id.walletLay)) {
//
//            openCloseDrawer();
//
//            startActivity(new Intent(BaseNavDrawer.this, Wallet.class));
//            finish();
//
//        } else if (v == binding.navView.findViewById(R.id.profileLay)) {
//
//            openCloseDrawer();
//            startActivity(new Intent(BaseNavDrawer.this, Profile.class));
//            finish();
//
//        } else if (v == binding.navView.findViewById(R.id.inviteLay)) {
//
//            openCloseDrawer();
//            startActivity(new Intent(BaseNavDrawer.this, Invitation.class));
//            finish();
//
//
//        } else if (v == binding.navView.findViewById(R.id.notificationLay)) {
//
//            openCloseDrawer();
//            startActivity(new Intent(BaseNavDrawer.this, Notifications.class));
//            finish();
//
//
//        } else if (v == binding.navView.findViewById(R.id.aboutUsLay)) {
//
//            openCloseDrawer();
//            startActivity(new Intent(BaseNavDrawer.this, AboutUs.class));
//
//        } else if (v == binding.navView.findViewById(R.id.csLay)) {
//
//            openCloseDrawer();
//            startActivity(new Intent(BaseNavDrawer.this, CreateNewRequestActivity.class));
//            finish();
//
//        } else if (v == binding.navView.findViewById(R.id.activeRide)) {
//
//            openCloseDrawer();
//            startActivity(new Intent(BaseNavDrawer.this, BookARideActivity.class));
//            finish();
//        } else if (v == binding.navView.findViewById(R.id.schedulesLay)) {
//
//            openCloseDrawer();
//            startActivity(new Intent(BaseNavDrawer.this, ScheduledRides.class));
//            finish();
//        }
    }

    public void openCloseDrawer() {

        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {

            binding.drawer.closeDrawer(GravityCompat.START);

        } else {

            binding.drawer.openDrawer(GravityCompat.START);

        }
    }

}
