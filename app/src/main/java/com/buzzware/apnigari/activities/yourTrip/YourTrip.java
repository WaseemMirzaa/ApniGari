package com.buzzware.apnigari.activities.yourTrip;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.apnigari.activities.base.BaseNavDrawer;
import com.buzzware.apnigari.activities.yourTrip.adapter.YourTripAdapter;
import com.buzzware.apnigari.databinding.ActivityYourTripsBinding;

public class YourTrip extends BaseNavDrawer {

    ActivityYourTripsBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivityYourTripsBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        init();

        setListener();
    }

    private void setListener() {

        mBinding.include.menuIV.setOnClickListener(view -> openCloseDrawer());

    }

    private void init() {

        mBinding.include.appBarTitle.setText("Your Trip");

        mBinding.listRV.setLayoutManager(new LinearLayoutManager(this));

        mBinding.listRV.setAdapter(new YourTripAdapter());

    }

}
