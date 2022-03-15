package com.buzzware.apnigari.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.buzzware.apnigari.activities.base.BaseActivity;
import com.buzzware.apnigari.activities.home.Home;
import com.buzzware.apnigari.activities.rentACar.RentACar;
import com.buzzware.apnigari.databinding.ActivitySelectTypeBinding;

public class SelectScreen extends BaseActivity {

    ActivitySelectTypeBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivitySelectTypeBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setListeners();

    }

    private void setListeners() {

        binding.bookRideIV.setOnClickListener(view -> {

            startActivity(new Intent(SelectScreen.this, Home.class));

        });

        binding.rentCarIV.setOnClickListener(view -> {

            startActivity(new Intent(SelectScreen.this, RentACar.class));

        });

    }


}
