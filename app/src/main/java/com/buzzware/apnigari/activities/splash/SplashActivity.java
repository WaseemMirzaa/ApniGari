package com.buzzware.apnigari.activities.splash;

import android.app.Activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.buzzware.apnigari.databinding.ActivitySplashBinding;

public class SplashActivity extends Activity {

    ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(binding.getRoot());

        setListeners();

    }

    private void setListeners() {

        binding.signUpTV.setOnClickListener(view -> moveToSignUp());

        binding.loginTV.setOnClickListener(view -> moveToLogin());

    }

    private void moveToSignUp() {



    }

    private void moveToLogin() {



    }
}
