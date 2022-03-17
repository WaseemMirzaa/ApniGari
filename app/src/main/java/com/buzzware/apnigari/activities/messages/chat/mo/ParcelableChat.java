package com.buzzware.apnigari.activities.messages.chat.mo;

import static com.buzzware.apnigari.activities.base.BaseActivity.getUserId;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableChat implements Parcelable {

    String conversationID = "";

    String selectedUserName = "";

    String selectedUserId = "";

    String currentUserId = "";

    String typeStatus = "false";

    String myImageUrl;

    String otherUserImageUrl;

    public ParcelableChat() {


    }

    public ParcelableChat(String conversationID, String selectedUserName, String selectedUserId, String currentUserId, String typeStatus, String myImageUrl, String otherUserImageUrl) {

        if (conversationID != null)
            this.conversationID = conversationID;
        else
            this.conversationID = "";

        if(selectedUserName != null)
            this.selectedUserName = selectedUserName;
        else
            this.selectedUserName = "";

        if(selectedUserId != null)
            this.selectedUserId = selectedUserId;
        else
            this.selectedUserId = "";

        if(selectedUserId != null)
            this.selectedUserId = selectedUserId;
        else
            this.selectedUserId = "";

        this.currentUserId = getUserId();

        if(typeStatus != null)
            this.typeStatus = typeStatus;
        else
            this.typeStatus = "false";

        if(myImageUrl != null)
            this.myImageUrl = typeStatus;
        else
            this.myImageUrl = "";

        if(otherUserImageUrl != null)
            this.otherUserImageUrl = otherUserImageUrl;
        else
            this.otherUserImageUrl = "";
    }

    protected ParcelableChat(Parcel in) {
        conversationID = in.readString();
        selectedUserName = in.readString();
        selectedUserId = in.readString();
        currentUserId = in.readString();
        typeStatus = in.readString();
        myImageUrl = in.readString();
        otherUserImageUrl = in.readString();
    }

    public static final Creator<ParcelableChat> CREATOR = new Creator<ParcelableChat>() {
        @Override
        public ParcelableChat createFromParcel(Parcel in) {
            return new ParcelableChat(in);
        }

        @Override
        public ParcelableChat[] newArray(int size) {
            return new ParcelableChat[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(conversationID);
        parcel.writeString(selectedUserName);
        parcel.writeString(selectedUserId);
        parcel.writeString(currentUserId);
        parcel.writeString(typeStatus);
        parcel.writeString(myImageUrl);
        parcel.writeString(otherUserImageUrl);
    }


    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public String getSelectedUserName() {
        return selectedUserName;
    }

    public void setSelectedUserName(String selectedUserName) {
        this.selectedUserName = selectedUserName;
    }

    public String getSelectedUserId() {
        return selectedUserId;
    }

    public void setSelectedUserId(String selectedUserId) {
        this.selectedUserId = selectedUserId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getTypeStatus() {
        return typeStatus;
    }

    public void setTypeStatus(String typeStatus) {
        this.typeStatus = typeStatus;
    }

    public String getMyImageUrl() {
        return myImageUrl;
    }

    public void setMyImageUrl(String myImageUrl) {
        this.myImageUrl = myImageUrl;
    }

    public String getOtherUserImageUrl() {
        return otherUserImageUrl;
    }

    public void setOtherUserImageUrl(String otherUserImageUrl) {
        this.otherUserImageUrl = otherUserImageUrl;
    }

    public static Creator<ParcelableChat> getCREATOR() {
        return CREATOR;
    }

}
