package com.leejordan.studygroupapp;

import java.util.Date;

public class Message {
    private String messageText;
    private String messageUser;
    private long messageTime;
    private String studyGroupId;

    public Message(String messageText, String messageUser, String studyGroupId) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.studyGroupId = studyGroupId;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public Message(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getStudyGroupId() {
        return studyGroupId;
    }

    public void setStudyGroupId(String studyGroupId) {
        this.studyGroupId = studyGroupId;
    }
}