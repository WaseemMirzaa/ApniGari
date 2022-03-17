package com.buzzware.apnigari.activities.messages.chatList;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.apnigari.activities.base.BaseNavDrawer;
import com.buzzware.apnigari.activities.messages.chatList.adapter.ChatListAdapter;
import com.buzzware.apnigari.activities.messages.chatList.mo.ConversationModel;
import com.buzzware.apnigari.activities.messages.chatList.vm.ChatListViewModel;
import com.buzzware.apnigari.databinding.ActivityChatListBinding;
import com.buzzware.apnigari.generic.GenericModelLiveData;

import java.util.List;

public class ChatList extends BaseNavDrawer {

    ActivityChatListBinding mBinding;

    ChatListViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivityChatListBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        init();

        setListener();
    }

    void init() {

        setLiveData();

        initViews();
    }

    @SuppressLint("SetTextI18n")
    private void initViews() {

        mBinding.include.appBarTitle.setText("Messages");

        mBinding.listRV.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setLiveData() {

//        model = new ViewModelProvider(this).get(ChatListViewModel.class);

//        model.getChatListLiveData().observe(ChatList.this, this::handleResponse);
    }

    private void handleResponse(GenericModelLiveData genericModelLiveData) {

        switch (genericModelLiveData.status) {

            case error:

                hideLoader();

                showErrorAlert(genericModelLiveData.errorMsg);

                break;

            case loading:

                showLoader();

                break;

            case success:

                hideLoader();

                List<ConversationModel> conversations = (List<ConversationModel>) genericModelLiveData.object;

                if (conversations != null)

                    setChatListAdapter(conversations);

                break;
        }
    }

    private void setChatListAdapter(List<ConversationModel> conversations) {

        mBinding.listRV.setAdapter(new ChatListAdapter(conversations, ChatList.this));

    }


    @Override
    protected void onResume() {

        super.onResume();

        mBinding.listRV.setAdapter(new ChatListAdapter(ChatList.this));
//        model.getConversationsList();

    }

    private void setListener() {

        mBinding.include.menuIV.setOnClickListener(view -> openCloseDrawer());

    }

}
