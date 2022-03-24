package com.buzzware.apnigari.activities.messages.chat.mo;

public class SendLastMessageModel {
    String content, fromID, messageId, toID, type;
    boolean isRead;
    long timestamp;

    public SendLastMessageModel() {

    }

    public SendLastMessageModel(String content, String fromID, String messageID, String toID, String type, boolean isRead, long timestamp) {
        this.content = content;
        this.fromID = fromID;
        this.messageId = messageID;
        this.toID = toID;
        this.type = type;
        this.isRead = isRead;
        this.timestamp = timestamp;
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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
