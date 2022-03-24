package com.buzzware.apnigari.activities.messages.chat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.apnigari.Firebase.FirebaseInstances;
import com.buzzware.apnigari.activities.auth.Login;
import com.buzzware.apnigari.activities.auth.vm.AuthViewModel;
import com.buzzware.apnigari.activities.base.BaseActivity;
import com.buzzware.apnigari.activities.messages.chat.adapter.MessagesAdapter;
import com.buzzware.apnigari.activities.messages.chat.mo.MessageModel;
import com.buzzware.apnigari.activities.messages.chat.mo.ParcelableChat;
import com.buzzware.apnigari.activities.messages.chat.vm.ChatViewModel;
import com.buzzware.apnigari.databinding.ActivityMessagesBinding;
import com.buzzware.apnigari.generic.GenericModelLiveData;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.UUID;

public class Chat extends BaseActivity {

    ActivityMessagesBinding mBinding;

    ParcelableChat parcelableChat;

    ChatViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivityMessagesBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        init();

        getDataFromExtra();

        setListener();

        setListenerOnRide();
    }

    private void setListenerOnRide() {

        model.setListenerOnRide(parcelableChat);

    }

    private void getDataFromExtra() {

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            parcelableChat = getIntent().getParcelableExtra("parcelableChat");

            if (parcelableChat.getTypeStatus().equals("false") || parcelableChat.getTypeStatus().equals("admin"))

                model.getConversation(parcelableChat.getConversationID(), FirebaseInstances.adminChatCollection, parcelableChat);

            else if (parcelableChat.getTypeStatus().equalsIgnoreCase("false")) {

                model.getConversation(parcelableChat.getConversationID(), FirebaseInstances.chatCollection, parcelableChat);

            } else {

                if (parcelableChat.getConversationID() == null)

                    parcelableChat.setConversationID(UUID.randomUUID().toString());

                model.checkAlreadyExists(parcelableChat);

            }

        }

    }

    private void getParcelableChatModel(GenericModelLiveData genericModelLiveData) {

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

                parcelableChat = (ParcelableChat) genericModelLiveData.object;

                if (parcelableChat.getTypeStatus().equals("admin")) {

                    model.loadMessages(FirebaseInstances.adminChatCollection, parcelableChat.getConversationID());

                } else {

                    model.loadMessages(FirebaseInstances.chatCollection, parcelableChat.getConversationID());

                }

                break;
        }
    }


    private void handleMessagesList(GenericModelLiveData genericModelLiveData) {

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

                List<MessageModel> messages = (List<MessageModel>) genericModelLiveData.object;

                setAdapter(messages);

                break;
        }
    }

    private void setAdapter(List<MessageModel> messages) {

        mBinding.listRV.setLayoutManager(new LinearLayoutManager(Chat.this));

        mBinding.listRV.setAdapter(new MessagesAdapter(Chat.this,messages,getUserId(), parcelableChat.getMyImageUrl(), parcelableChat.getOtherUserImageUrl()));

    }


    private void setListener() {

        mBinding.include.backIV.setOnClickListener(view -> finish());

        mBinding.sendIV.setOnClickListener(view -> {

            if (!mBinding.sendMessageET.getText().toString().isEmpty()) {

                sendMessage(mBinding.sendMessageET.getText().toString());

            }
        });
    }

    private void sendMessage(String s) {

        mBinding.sendMessageET.setText("");

        model.sendMessage(s, parcelableChat);

    }

    @SuppressLint("SetTextI18n")
    private void init() {

        mBinding.include.appBarTitle.setText("Messages");

        model = new ViewModelProvider(this).get(ChatViewModel.class);

        model.getParcelableChatModel().observe(Chat.this, this::getParcelableChatModel);

        model.getMessagesDataModel().observe(Chat.this, this::handleMessagesList);

        model.getAlreadyExistsCheckData().observe(Chat.this, this::handleAlreadyExistsResponse);

        model.getRideListenerData().observe(Chat.this, this::rideListenerResponse);

    }

    private void rideListenerResponse(GenericModelLiveData genericModelLiveData) {


        switch (genericModelLiveData.status) {

            case error:

                break;

            case loading:

//                showLoader();

                break;

            case success:

                showChatDeletedPopup();

                break;
        }

    }

    AlertDialog messagesDeletedPopup;

    private void showChatDeletedPopup() {

        if (messagesDeletedPopup != null && messagesDeletedPopup.isShowing())

            return;
        messagesDeletedPopup = new AlertDialog.Builder(Chat.this)
                .setCancelable(false)
                .setTitle("Alert")
                .setMessage("Ride is completed or cancelled. This chat has been deleted")
                .setPositiveButton("OK", (dialogInterface, i) -> {

                    dialogInterface.dismiss();
                    finish();

                }).create();

        messagesDeletedPopup.show();
    }

    private void handleAlreadyExistsResponse(GenericModelLiveData genericModelLiveData) {

        switch (genericModelLiveData.status) {

            case error:

                hideLoader();
//
                showErrorAlert(genericModelLiveData.errorMsg);
//
                break;

            case loading:

                showLoader();

                break;

            case success:

//                hideLoader();

                List<MessageModel> messages = (List<MessageModel>) genericModelLiveData.object;

                setAdapter(messages);

                break;
        }
    }

}
