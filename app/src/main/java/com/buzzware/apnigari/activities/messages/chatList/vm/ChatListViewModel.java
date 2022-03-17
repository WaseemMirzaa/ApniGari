package com.buzzware.apnigari.activities.messages.chatList.vm;

import static com.buzzware.apnigari.activities.base.BaseActivity.getUserId;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.buzzware.apnigari.Firebase.FirebaseInstances;
import com.buzzware.apnigari.activities.auth.vm.mo.User;
import com.buzzware.apnigari.activities.messages.chatList.mo.ConversationModel;
import com.buzzware.apnigari.activities.messages.chatList.mo.LastMessageModel;
import com.buzzware.apnigari.generic.GenericModelLiveData;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatListViewModel extends ViewModel {

    private MutableLiveData<GenericModelLiveData> data;

    public LiveData<GenericModelLiveData> getChatListLiveData() {

        if (data == null) {

            data = new MutableLiveData<>();

        }

        return data;
    }

    public void getConversationsList() {

        final ArrayList<LastMessageModel> list = new ArrayList<>();

        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.loading, null));

        FirebaseInstances.chatCollection
                .whereEqualTo("participants." + getUserId(), true)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {

                            LastMessageModel lastMessageModel = snapshot.get("lastMessage", LastMessageModel.class);

                            if(lastMessageModel != null) {

                                lastMessageModel.conversationId = snapshot.getId();

                                list.add(lastMessageModel);
                            }
                        }

                        filterList(list);

                    } else {

                        if (task.getException() == null)

                            return;

                        if ((task.getException().getLocalizedMessage() != null))

                            data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, task.getException().getLocalizedMessage()));

                    }

                });

    }

    private void filterList(ArrayList<LastMessageModel> list) {

        List<ConversationModel> conversations = new ArrayList<>();

        FirebaseInstances.usersCollection
                . get()
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()) {

                        for(DocumentSnapshot documentSnapshot: task.getResult().getDocuments()) {

                            User user = documentSnapshot.toObject(User.class);

                            if(user != null) {

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
        model.name = user.firstName+" "+user.lastName;
        model.image = user.image;
        model.lastMessage = lastMessageModel.content;
        model.id = lastMessageModel.conversationId;
        model.toID = lastMessageModel.toID;

        return model;

    }

}
