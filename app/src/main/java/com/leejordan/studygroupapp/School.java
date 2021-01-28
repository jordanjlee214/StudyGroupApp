package com.leejordan.studygroupapp;

import java.util.HashMap;
import java.util.Map;

public class School {
    private String name;
    private String city;
    private String state;
    private String schoolID;

    public School(String name, String city, String state, String schoolID){
        this.name = name;
        this.city = city;
        this.state = state;
        this.schoolID = schoolID;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Map<String, Object> toMap(){
        HashMap schoolMap = new HashMap();
        schoolMap.put("schoolName", name);
        schoolMap.put("schoolCity", city);
        schoolMap.put("schoolState", state);
        schoolMap.put("schoolID", schoolID);
        return schoolMap;
    }

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }
}
