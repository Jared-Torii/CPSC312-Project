package com.example.cpsc312project;

import java.util.List;

public class Game {

    private String lobbyCode;
    private User firstUser;
    private User secondUser;
    private int numUsers;
    private int time;
    private List<String> sharedGrid;
    private List<String> firstUserWords;
    private List<String> secondUserWords;

    public Game() {
    }

    public Game(String lobbyCode, User firstUser, User secondUser, int numUsers, int time,
                List<String> sharedGrid, List<String> firstUserWords,
                List<String> secondUserWords) {
        this.lobbyCode = lobbyCode;
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.numUsers = numUsers;
        this.time = time;
        this.sharedGrid = sharedGrid;
        this.firstUserWords = firstUserWords;
        this.secondUserWords = secondUserWords;
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

    public List<String> getSharedGrid() {
        return sharedGrid;
    }

    public void setSharedGrid(List<String> sharedGrid) {
        this.sharedGrid = sharedGrid;
    }

    public List<String> getFirstUserWords() {
        return firstUserWords;
    }

    public void setFirstUserWords(List<String> firstUserWords) {
        this.firstUserWords = firstUserWords;
    }

    public List<String> getSecondUserWords() {
        return secondUserWords;
    }

    public void setSecondUserWords(List<String> secondUserWords) {
        this.secondUserWords = secondUserWords;
    }

}