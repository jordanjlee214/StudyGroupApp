package com.leejordan.studygroupapp;

import java.security.acl.Group;

public class GroupListItem {
    private String groupName, groupCreator, subject, classType, profilePic, groupID;
    private int members;


    public GroupListItem(){

    }
    public GroupListItem(String groupName, int members, String groupCreator, String subject, String classType, String profilePic, String groupID){
        this.groupName = groupName;
        this.members = members;
        this.groupCreator = groupCreator;
        this.subject = subject;
        this.classType = classType;
        this.profilePic = profilePic;
        this.groupID = groupID;

    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public String getGroupCreator() {
        return groupCreator;
    }

    public void setGroupCreator(String groupCreator) {
        this.groupCreator = groupCreator;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }
}
