package com.buzzware.apnigari.activities.wallet;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.apnigari.activities.base.BaseNavDrawer;
import com.buzzware.apnigari.activities.wallet.adapter.WalletAdapter;
import com.buzzware.apnigari.databinding.ActivityWalletBinding;

public class Wallet extends BaseNavDrawer {

    ActivityWalletBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivityWalletBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        init();

        setListener();
    }

    private void setListener() {

        mBinding.includeAppBar.menuIV.setOnClickListener(view -> openCloseDrawer());

    }

    private void init() {

        mBinding.listCancelRideRV.setLayoutManager(new LinearLayoutManager(Wallet.this));

        mBinding.listCancelRideRV.setAdapter(new WalletAdapter());

        mBinding.includeAppBar.appBarTitle.setText("Wallet");

    }

}
