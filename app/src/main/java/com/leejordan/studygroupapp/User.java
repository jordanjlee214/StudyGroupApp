package com.leejordan.studygroupapp;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username, firstName, lastName, birthday, gender, school;
    private String bio;
    private String userID;
    private String profilePic;
    private int groups;
    private HashMap<String, Object> groupList;
    //Profile images
    //Group info
    //Classes, etc.

    public User(){
        username = "";
        firstName = "";
        lastName = "";
        birthday = "";
        gender = "";
        school = "";
        bio = "";
        userID = "";
        groups = 0;
        profilePic = "https://firebasestorage.googleapis.com/v0/b/studygroupapp-33f55.appspot.com/o/profile_pics%2Fdefault.jpg?alt=media&token=d3d60c39-f634-47d0-9084-b5b1a4163ea5";
        groupList = new HashMap<>();
    }

    public User(String u, String fN, String lN, String b, String g, String s, String profileBio, String uID){
        username = u;
        firstName = fN;
        lastName = lN;
        birthday = b;
        gender = g;
        school = s;
        bio = profileBio;
        userID = uID;
        groups = 0;
        profilePic = "https://firebasestorage.googleapis.com/v0/b/studygroupapp-33f55.appspot.com/o/profile_pics%2Fdefault.jpg?alt=media&token=d3d60c39-f634-47d0-9084-b5b1a4163ea5";
        groupList = new HashMap<>();
    }

    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBio() {
        return bio;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public String getSchool() {
        return school;
    }


    public int getGroups() {
        return groups;
    }

    public HashMap<String, Object> getGroupList() {
        return groupList;
    }

    public void setGroupList(HashMap<String, Object> groupList) {
        this.groupList = groupList;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setGroups(int groups) {
        this.groups = groups;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public void setSchool(String school) {
        this.school = school;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Map<String, Object> toMap(){
        HashMap userData = new HashMap();
        userData.put("username", username);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("gender", gender);
        userData.put("birthday", birthday);
        userData.put("school", school);
        userData.put("bio", bio);
        userData.put("groups", groups);
        userData.put("userID", userID);
        userData.put("groupList", groupList);
      //  userData.put("profilePic", profilePic);
        //default bio for profiles
        return userData;
    }
}
