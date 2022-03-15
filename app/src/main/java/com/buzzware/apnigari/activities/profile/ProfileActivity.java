package com.buzzware.apnigari.activities.profile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.buzzware.apnigari.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityProfileBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setView();

        setListener();

    }

    private void setListener() {

        binding.backIV.setOnClickListener(v->{
            finish();
        });
        binding.confirmBTN.setOnClickListener(v->{
            finish();
        });

    }

    private void setView() {
        binding.appBarTitle.setText("Edit profile");

    }


}