package com.leejordan.studygroupapp;

import java.util.HashMap;
import java.util.Map;

public class StudyGroup {
    private String groupID;
    private String groupName;
    private Map<String,String> users;
    private Map<String, String> requestedUsers;
    private Map<String, String> invitedUsers;
    private String description;
    private int members;
    private int memberLimit;
    private boolean isPublic;
    private String schoolName; //will gather any school info from database
    private String schoolCity;
    private String schoolState;
    private String subject; //will gather any subject/class info from database
    private String classType;
    private String teacher; //this doesn't need to be filled out, if user chooses not to fill it out, default is NONE
    private String periodNumber; //also may not be filled out
    private boolean isFilled;
    private String groupCreator;

    private com.leejordan.studygroupapp.Message[] messages;

    public StudyGroup() {
    }

    //this constructor will be used when a group is first created and they aren't putting teacher or period number
    public StudyGroup(String groupID, String groupName, String groupCreator, String description, int members, int memberLimit, boolean isPublic, String schoolName, String schoolCity, String schoolState, String subject, String classType, HashMap<String, String> users){
        this.groupID = groupID;
        this.groupName = groupName;
        this.description = description;
        this.members = members;
        this.memberLimit = memberLimit;
        this.isPublic = isPublic;
        this.schoolName = schoolName;
        this.schoolCity = schoolCity;
        this.schoolState = schoolState;
        this.subject = subject;
        this.classType = classType;
        this.users = users;
        this.groupCreator = groupCreator;

        requestedUsers = new HashMap<>();
        invitedUsers = new HashMap<>();
        teacher = "NONE";
        periodNumber = "NONE";
        isFilled = false;

        //TODO: fill in array messages from firebase database here
    }

    //this constructor will be used when a group is first created and they put teacher, but no period number
    public StudyGroup(String groupID, String groupName, String groupCreator, String description, int members, int memberLimit, boolean isPublic, String schoolName, String schoolCity, String schoolState, String subject, String classType, HashMap<String, String> users, String teacher){
        this.groupID = groupID;
        this.groupName = groupName;
        this.description = description;
        this.members = members;
        this.memberLimit = memberLimit;
        this.isPublic = isPublic;
        this.schoolName = schoolName;
        this.schoolCity = schoolCity;
        this.schoolState = schoolState;
        this.subject = subject;
        this.classType = classType;
        this.users = users;
        this.teacher = teacher;
this.groupCreator = groupCreator;

        requestedUsers = new HashMap<>();
        invitedUsers = new HashMap<>();
        periodNumber = "NONE";
        isFilled = false;

        //TODO: fill in array messages from firebase database here
    }

    //this constructor will be used when a group is first created and they put both teacher and period number
    public StudyGroup(String groupID, String groupName, String groupCreator, String description, int members, int memberLimit, boolean isPublic, String schoolName, String schoolCity, String schoolState, String subject, String classType, HashMap<String, String> users, String teacher, String periodNumber){
        this.groupID = groupID;
        this.groupName = groupName;
        this.description = description;
        this.members = members;
        this.memberLimit = memberLimit;
        this.isPublic = isPublic;
        this.schoolName = schoolName;
        this.schoolCity = schoolCity;
        this.schoolState = schoolState;
        this.subject = subject;
        this.classType = classType;
        this.users = users;
        this.teacher = teacher;
        this.periodNumber = periodNumber;
        this.groupCreator = groupCreator;

        requestedUsers = new HashMap<>();
        invitedUsers = new HashMap<>();
        isFilled = false;

        //TODO: fill in array messages from firebase database here
    }

    //this constructor lets you fill in all fields
    public StudyGroup(String groupID, String groupName, String groupCreator, String description, int members, int memberLimit, boolean isPublic, String schoolName, String schoolCity, String schoolState, String subject, String classType, HashMap<String, String> users, String teacher, String periodNumber, HashMap<String, String> requestedUsers, HashMap<String, String> invitedUsers){
        this.groupID = groupID;
        this.groupName = groupName;
        this.description = description;
        this.members = members;
        this.memberLimit = memberLimit;
        this.isPublic = isPublic;
        this.schoolName = schoolName;
        this.schoolCity = schoolCity;
        this.schoolState = schoolState;
        this.subject = subject;
        this.classType = classType;
        this.users = users;
        this.teacher = teacher;
        this.requestedUsers = requestedUsers;
        this.invitedUsers = invitedUsers;
        this.periodNumber = periodNumber;
        this.groupCreator = groupCreator;
        if (members < memberLimit){
            isFilled = false;
        }
        else{
            isFilled = true;
        }

        //TODO: fill in array messages from firebase database here
    }


    public String getGroupID() {
        return groupID;
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

    public Map<String, String> getRequestedUsers() {
        return requestedUsers;
    }

    public Map<String, String> getInvitedUsers() {
        return invitedUsers;
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

    public void setRequestedUsers(Map<String, String> requestedUsers) {
        this.requestedUsers = requestedUsers;
    }

    public void setInvitedUsers(Map<String, String> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public void addMember(String memberName, String memberId){
        users.put(memberId,memberName);
        members++;
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public int getMemberLimit() {
        return memberLimit;
    }

    public void setMemberLimit(int memberLimit) {
        this.memberLimit = memberLimit;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolCity() {
        return schoolCity;
    }

    public void setSchoolCity(String schoolCity) {
        this.schoolCity = schoolCity;
    }

    public String getSchoolState() {
        return schoolState;
    }

    public void setSchoolState(String schoolState) {
        this.schoolState = schoolState;
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

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getPeriodNumber() {
        return periodNumber;
    }

    public void setPeriodNumber(String periodNumber) {
        this.periodNumber = periodNumber;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
    }

    //use when creating groups
    public Map<String, Object> toMap(){
        HashMap groupData = new HashMap();
        groupData.put("groupName", groupName);
        groupData.put("description", description);
        groupData.put("memberLimit", memberLimit);
        groupData.put("members", members);
        groupData.put("isPublic", isPublic);
        groupData.put("isFilled", isFilled);
        groupData.put("schoolName", schoolName);
        groupData.put("schoolCity", schoolCity);
        groupData.put("schoolState", schoolState);
        groupData.put("subject", subject);
        groupData.put("classType", classType);
        groupData.put("teacher", teacher);
        groupData.put("periodNumber", periodNumber);
        groupData.put("users", users);
        groupData.put("requestedUsers", requestedUsers);
        groupData.put("invitedUsers", invitedUsers);
        groupData.put("groupID", groupID);
        groupData.put("groupCreator", groupCreator);

        //  userData.put("profilePic", profilePic);
        //default bio for profiles
        return groupData;
    }

    public String getGroupCreator() {
        return groupCreator;
    }

    public void setGroupCreator(String groupCreator) {
        this.groupCreator = groupCreator;
    }


}