package com.buzzware.apnigari.activities.setting;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.buzzware.apnigari.activities.base.BaseNavDrawer;
import com.buzzware.apnigari.activities.auth.Login;
import com.buzzware.apnigari.activities.profile.ProfileActivity;
import com.buzzware.apnigari.databinding.ActivitySettingsBinding;

public class SettingActivity extends BaseNavDrawer {

    ActivitySettingsBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivitySettingsBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        init();

        setListener();
    }

    private void setListener() {

        mBinding.includeAppBar.menuIV.setOnClickListener(view -> openCloseDrawer());
        mBinding.accountCL.setOnClickListener(v->{
            startActivity(new Intent(SettingActivity.this, ProfileActivity.class));
        });
        mBinding.logoutCL.setOnClickListener(v->{
            startActivity(new Intent(SettingActivity.this, Login.class));
        });

    }

    private void init() {

        mBinding.includeAppBar.appBarTitle.setText("Setting");

    }
}