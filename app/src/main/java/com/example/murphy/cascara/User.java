package com.example.murphy.cascara;

import java.util.ArrayList;

/** Simple Java Object for a user */
public class User {
    private int userID;
    private String firstName;
    private String lastName;
    private int totalCheckins;
    private String email;
    private ArrayList<CheckIn> checkins;

    public User(int id, String fname, String lname, String email, int checkins) {
        this.userID = id;
        this.firstName = fname;
        this.lastName = lname;
        this.email = email;
        this.totalCheckins = checkins;
        this.checkins = new ArrayList<>();
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getTotalCheckins() {
        return totalCheckins;
    }

    public void setTotalCheckins(int totalCheckins) {
        this.totalCheckins = totalCheckins;
    }
}
