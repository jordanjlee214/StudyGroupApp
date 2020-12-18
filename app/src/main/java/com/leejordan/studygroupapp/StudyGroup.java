package com.leejordan.studygroupapp;

import java.util.HashMap;
import java.util.Map;

public class StudyGroup {
    private String groupId;
    private String groupName;
    private Map<String,String> memberIdtoName;
    private String description;
    private int members;
    private com.leejordan.studygroupapp.Message[] messages;

    public StudyGroup() {
    }
    public StudyGroup(String groupId, String groupName, HashMap<String,String> memberIdtoName, String description, int members){
        this.groupId = groupId;
        this.groupName = groupName;
        this.description = description;
        this.members = members;
        this.memberIdtoName = memberIdtoName;

        //TODO: fill in array messages from firebase database here
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getDescription() {
        return description;
    }

    public int getMembers() {
        return members;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public void addMember(String memberName, String memberId){
        memberIdtoName.put(memberId,memberName);
        members++;
    }
}
