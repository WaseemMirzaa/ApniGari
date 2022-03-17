package com.buzzware.apnigari.activities.messages.chat.vm;

import static com.buzzware.apnigari.activities.base.BaseActivity.getUserId;

import android.app.AlertDialog;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.buzzware.apnigari.Firebase.FirebaseInstances;
import com.buzzware.apnigari.activities.auth.vm.mo.User;
import com.buzzware.apnigari.activities.messages.chat.mo.MessageModel;
import com.buzzware.apnigari.activities.messages.chat.mo.ParcelableChat;
import com.buzzware.apnigari.activities.messages.chatList.mo.ConversationModel;
import com.buzzware.apnigari.activities.messages.chatList.mo.LastMessageModel;
import com.buzzware.apnigari.commonModels.ride.RideModel;
import com.buzzware.apnigari.generic.GenericModelLiveData;
import com.buzzware.apnigari.utils.AppConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<GenericModelLiveData> data, alreadyExistsOrNotData;
    private MutableLiveData<GenericModelLiveData> messagesData, rideListenerData;

    public LiveData<GenericModelLiveData> getParcelableChatModel() {

        if (data == null) {

            data = new MutableLiveData<>();

        }

        return data;
    }
    public LiveData<GenericModelLiveData> getRideListenerData() {

        if (rideListenerData == null) {

            rideListenerData = new MutableLiveData<>();

        }

        return rideListenerData;
    }
    public LiveData<GenericModelLiveData> getAlreadyExistsCheckData() {

        if (alreadyExistsOrNotData == null) {

            alreadyExistsOrNotData = new MutableLiveData<>();

        }

        return alreadyExistsOrNotData;
    }

    public LiveData<GenericModelLiveData> getMessagesDataModel() {

        if (messagesData == null) {

            messagesData = new MutableLiveData<>();

        }

        return messagesData;
    }

    public void getConversation(String conversationID, CollectionReference reference, ParcelableChat parcelableChat) {

        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.loading, null));

        reference.document(conversationID)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.getResult() != null) {

                        LastMessageModel lastMessageModel = task.getResult().get("lastMessage", LastMessageModel.class);

                        if (lastMessageModel != null) {

                            parcelableChat.setCurrentUserId(getUserId());
                            parcelableChat.setSelectedUserId(lastMessageModel.fromID);

                            if (parcelableChat.getCurrentUserId().equalsIgnoreCase(lastMessageModel.fromID)) {

                                parcelableChat.setSelectedUserId(lastMessageModel.toID);

                            }

                            parcelableChat.setConversationID(conversationID);

                            getMyImage(parcelableChat);

                        }

                    } else {

                        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, "No Conversation Found"));

                    }
                });

    }

    public void getMyImage(ParcelableChat parcelableChat) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firebaseFirestore.collection("Users").document(parcelableChat.getCurrentUserId());

        reference
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        User user = task.getResult().toObject(User.class);

                        if (user != null) {

                            parcelableChat.setMyImageUrl(user.image);

                        }

                    }

                    getOtherUserImage(parcelableChat);

                });
    }

    public void getOtherUserImage(ParcelableChat parcelableChat) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firebaseFirestore.collection("Users").document(parcelableChat.getSelectedUserId());

        reference
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        User user = task.getResult().toObject(User.class);

                        if (user != null) {

                            parcelableChat.setOtherUserImageUrl(user.image);

                        }

                    }

                    data.postValue(new GenericModelLiveData(parcelableChat, GenericModelLiveData.Status.success, null));

                });

    }


    private void filterList(ArrayList<LastMessageModel> list) {

        List<ConversationModel> conversations = new ArrayList<>();

        FirebaseInstances.usersCollection
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                            User user = documentSnapshot.toObject(User.class);

                            if (user != null) {

                                user.id = documentSnapshot.getId();

                                for (int i = 0; i < list.size(); i++) {

                                    String otherUserId = list.get(i).fromID;

                                    if (!getUserId().equalsIgnoreCase(list.get(i).fromID))

                                        otherUserId = list.get(i).toID;


                                    if (user.id.equalsIgnoreCase(otherUserId)) {

                                        ConversationModel conversation = getConversationModel(list.get(i), user);

                                        conversations.add(conversation);
                                    }

                                }
                            }

                        }

                    }

                    data.postValue(new GenericModelLiveData(conversations, GenericModelLiveData.Status.success, null));

                });

    }


    private ConversationModel getConversationModel(LastMessageModel lastMessageModel, User user) {

        ConversationModel model = new ConversationModel();

        model.conversationID = lastMessageModel.conversationId;
        model.name = user.firstName + " " + user.lastName;
        model.image = user.image;
        model.lastMessage = lastMessageModel.content;
        model.id = lastMessageModel.conversationId;
        model.toID = lastMessageModel.toID;

        return model;

    }

    ListenerRegistration loadMessagesListener;

    ListenerRegistration adminListener;

    public void loadMessages(CollectionReference reference, String conversationID) {

        List<MessageModel> messageModels = new ArrayList<>();

        adminListener = reference.document(conversationID).collection("Conversations").addSnapshotListener((value, error) -> {

            if (value != null) {

                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {

                    MessageModel messageModel = documentSnapshot.toObject(MessageModel.class);

                    messageModels.add(messageModel);

                }

                messagesData.postValue(new GenericModelLiveData(messageModels, GenericModelLiveData.Status.success, null));

            } else {

                messagesData.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, error != null ? error.getMessage() : "Unable to load messages"));

            }

        });
    }

    public void deInit() {

        adminListener = null;

        loadMessagesListener = null;

    }


    public void checkAlreadyExists(ParcelableChat parcelableChat) {

        final ArrayList<LastMessageModel> list = new ArrayList<>();

        FirebaseInstances.chatCollection
                .whereEqualTo("participants." + parcelableChat.getSelectedUserId(), true)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {

                            LastMessageModel lastMessageModel = snapshot.get("lastMessage", LastMessageModel.class);

                            if(lastMessageModel != null) {

                                lastMessageModel.conversationId = snapshot.getId();

                                if (lastMessageModel.conversationId.equalsIgnoreCase(parcelableChat.getConversationID()))

                                    list.add(lastMessageModel);
                            }
                        }


                        if (list.size() > 0) {

                            for (int i = 0; i < list.size(); i++) {

                                if (list.get(i).getToID().equals(parcelableChat.getSelectedUserId()) || list.get(i).getFromID().equals(parcelableChat.getSelectedUserId())) {

                                    parcelableChat.setTypeStatus("false");

                                    getMyImage(parcelableChat);

                                    return;
                                }
                            }
                        }

                    } else {

                        if (task.getException() == null)

                            return;

                        if ((task.getException().getLocalizedMessage() != null))

                            alreadyExistsOrNotData.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, task.getException().getLocalizedMessage()));
                    }

                });
    }

    ListenerRegistration eventListener;
    public void setListenerOnRide(ParcelableChat parcelableChat) {

        FirebaseInstances.bookingsCollection.document(parcelableChat.getConversationID())
                .addSnapshotListener((value, error) -> {

                    if (value != null) {

                        RideModel rideModel = value.toObject(RideModel.class);

                        if (rideModel != null) {

                            rideModel.id = value.getId();

                            if (AppConstants.RideStatus.RE_BOOKED.equalsIgnoreCase(rideModel.status) ||
                                    AppConstants.RideStatus.CANCELLED.equalsIgnoreCase(rideModel.status) ||
                                    AppConstants.RideStatus.RIDE_COMPLETED.equalsIgnoreCase(rideModel.status) ||
                                    AppConstants.RideStatus.RATED.equalsIgnoreCase(rideModel.status) ||
                                    AppConstants.RideStatus.DISPUTE.equalsIgnoreCase(rideModel.status) ||
                                    AppConstants.RideStatus.DISPUTED.equalsIgnoreCase(rideModel.status)
                            ) {

                                eventListener.remove();

                                eventListener = null;

                                rideListenerData.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.success, null));

                            }
                        }
                    }
                });
    }
}
