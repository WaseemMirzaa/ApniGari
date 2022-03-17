package com.buzzware.apnigari.activities.carBooking;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.buzzware.apnigari.activities.base.BaseActivity;
import com.buzzware.apnigari.databinding.ActivityBookingBinding;

public class BookACar extends BaseActivity {

    ActivityBookingBinding mBinding;

    String[] country = { "Select Car Type", "GO", "GO Plus", "Business"};

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

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter<String> aa = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,country);

        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBinding.spinner.setAdapter(aa);

    }

}
