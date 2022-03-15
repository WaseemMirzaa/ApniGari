package com.buzzware.apnigari.activities.rentACar;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.buzzware.apnigari.activities.base.BaseNavDrawer;
import com.buzzware.apnigari.activities.carBooking.BookACar;
import com.buzzware.apnigari.activities.rentACar.adapter.RentACarAdapter;
import com.buzzware.apnigari.databinding.ActivityRentACarBinding;

public class RentACar extends BaseNavDrawer {

    ActivityRentACarBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivityRentACarBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        init();

        setListener();
    }

    private void setListener() {

        mBinding.include.menuIV.setOnClickListener(view -> openCloseDrawer());
        mBinding.bookTV.setOnClickListener(view -> {

            startActivity(new Intent(RentACar.this, BookACar.class));

        });

    }

    private void init() {

        mBinding.include.appBarTitle.setText("Rent a Car");

        mBinding.listRV.setLayoutManager(new GridLayoutManager(RentACar.this, 2));

        mBinding.listRV.setAdapter(new RentACarAdapter());
    }

}
