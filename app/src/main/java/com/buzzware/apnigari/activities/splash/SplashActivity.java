package com.buzzware.apnigari.activities.splash;

import static com.buzzware.apnigari.activities.base.BaseActivity.getUserId;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.buzzware.apnigari.activities.auth.Login;
import com.buzzware.apnigari.activities.auth.SignUp;
import com.buzzware.apnigari.activities.home.Home;
import com.buzzware.apnigari.databinding.ActivitySplashBinding;
import com.google.firebase.FirebaseApp;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(SplashActivity.this);

        setListeners();

        if(!getUserId().isEmpty()){

            startActivity(new Intent(SplashActivity.this, Home.class));

            finish();
        }

    }

    private void setListeners() {

        binding.loginTV.setOnClickListener(view -> moveToLoginActivity());
        binding.signUpTV.setOnClickListener(view -> moveToSignUpActivity());

    }

    private void moveToLoginActivity() {

        startActivity(new Intent(SplashActivity.this, Login.class));

        finish();
    }

    private void moveToSignUpActivity() {

        startActivity(new Intent(SplashActivity.this, SignUp.class));

        finish();
    }
}