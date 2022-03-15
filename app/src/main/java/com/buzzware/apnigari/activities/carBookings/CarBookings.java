package com.buzzware.apnigari.activities.carBookings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.apnigari.activities.base.BaseNavDrawer;
import com.buzzware.apnigari.activities.carBookings.adapter.CarBookingsAdapter;
import com.buzzware.apnigari.databinding.ActivityBookingsListBinding;

public class CarBookings extends BaseNavDrawer {

    ActivityBookingsListBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivityBookingsListBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        init();

        setListener();
    }

    private void setListener() {

        mBinding.include.menuIV.setOnClickListener(view -> openCloseDrawer());

    }

    private void init() {

        mBinding.include.appBarTitle.setText("Bookings");

        mBinding.listRV.setLayoutManager(new LinearLayoutManager(CarBookings.this));

        mBinding.listRV.setAdapter(new CarBookingsAdapter());
    }

}
