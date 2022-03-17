package com.buzzware.apnigari.activities.messages.chatList.mo;

public class LastMessageModel {

    public String content, fromID, toID, type;

    public String conversationId;

    public LastMessageModel() {
    }

    public LastMessageModel(String content, String fromID, String toID, String type) {
        this.content = content;
        this.fromID = fromID;
        this.toID = toID;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
