package com.leejordan.studygroupapp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message {
    private String messageText;
    private String messageUserID;
    private long messageTime;
    private String studyGroupId;

    public Message(String messageText, String messageUser, String studyGroupId) {
        this.messageText = messageText;
        this.messageUserID = messageUser;
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

    public String getMessageUserID() {
        return messageUserID;
    }

    public void setMessageUserID(String messageUser) {
        this.messageUserID = messageUser;
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

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("messageUserID", messageUserID);
        map.put("messageText", messageText );
        map.put("messageTime", messageTime);
        map.put("groupID", studyGroupId);
        return map;
    }

}