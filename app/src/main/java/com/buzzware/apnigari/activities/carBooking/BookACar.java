package com.buzzware.apnigari.activities.carBooking;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.buzzware.apnigari.activities.base.BaseActivity;
import com.buzzware.apnigari.databinding.ActivityBookingBinding;

public class BookACar extends BaseActivity {

    ActivityBookingBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivityBookingBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        init();

        setListener();
    }

    private void setListener() {

        mBinding.include.backIV.setOnClickListener(view -> finish());

    }

    private void init() {

//        mBinding.include.appBarTitle.setText("Rent a Car");

    }

}
