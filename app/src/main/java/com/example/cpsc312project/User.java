package com.example.cpsc312project;

public class User {

    private String username;
    private int timeSetting;

    public User() {
    }

    public User(String username, int timeSetting) {
        this.username = username;
        this.timeSetting = timeSetting;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTimeSetting() {
        return timeSetting;
    }

    public void setTimeSetting(int timeSetting) {
        this.timeSetting = timeSetting;
    }

}