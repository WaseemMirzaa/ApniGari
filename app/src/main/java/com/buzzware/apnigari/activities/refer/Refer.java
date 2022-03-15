package com.buzzware.apnigari.activities.refer;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.buzzware.apnigari.activities.base.BaseNavDrawer;
import com.buzzware.apnigari.databinding.ActivityReferBinding;

public class Refer extends BaseNavDrawer {

    ActivityReferBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivityReferBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        init();

        setListener();
    }

    private void setListener() {

        mBinding.include.menuIV.setOnClickListener(view -> openCloseDrawer());

    }

    private void init() {

        mBinding.include.appBarTitle.setText("Refer");

    }

}
