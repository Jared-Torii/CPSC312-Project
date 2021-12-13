package com.example.cpsc312project;

public class Game {

    private String lobbyCode;
    private User firstUser;
    private User secondUser;
    private int numUsers;
    private int time;

    public Game() {
    }

    public Game(String lobbyCode, User firstUser, User secondUser, int numUsers, int time) {
        this.lobbyCode = lobbyCode;
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.numUsers = numUsers;
        this.time = time;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(User firstUser) {
        this.firstUser = firstUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(User secondUser) {
        this.secondUser = secondUser;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public int getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

}