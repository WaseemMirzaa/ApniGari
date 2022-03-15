package com.buzzware.apnigari.activities.messages.chatList;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.apnigari.activities.base.BaseNavDrawer;
import com.buzzware.apnigari.activities.messages.chatList.adapter.ChatListAdapter;
import com.buzzware.apnigari.databinding.ActivityChatListBinding;

public class ChatList extends BaseNavDrawer {

    ActivityChatListBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivityChatListBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        init();

        setListener();
    }

    private void setListener() {

        mBinding.include.menuIV.setOnClickListener(view -> openCloseDrawer());

    }

    private void init() {

        mBinding.include.appBarTitle.setText("Messages");

        mBinding.listRV.setLayoutManager(new LinearLayoutManager(this));

        mBinding.listRV.setAdapter(new ChatListAdapter());
    }

}
