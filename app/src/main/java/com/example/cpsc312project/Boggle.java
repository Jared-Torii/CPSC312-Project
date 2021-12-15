package com.example.cpsc312project;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Boggle {

    private static final String[] boggleDice = {"AAEEGN", "ABBJOO", "ACHOPS", "AFFKPS",
            "AOOTTW", "CIMOTU", "DEILRX", "DELRVY",
            "DISTTY", "EEGHNW", "EEINSU", "EHRTVW",
            "EIOSST", "ELRTTY", "HIMNUQu", "HLNNRZ"};

    private HashMap<Integer, Integer> lengthToPoints;
    private String[][] grid;
    private List<String> correctWords;
    private List<String> alreadyUsedWords;
    private StringBuilder currString;
    private int currPoints;
    private Context context;

    private int lastX;
    private int lastY;

    public Boggle(Context context) {
        lengthToPoints = new HashMap<Integer, Integer>();
        lengthToPoints.put(3, 1);
        lengthToPoints.put(4, 1);
        lengthToPoints.put(5, 2);
        lengthToPoints.put(6, 3);
        lengthToPoints.put(7, 5);
        lengthToPoints.put(8, 11);
        lengthToPoints.put(9, 11);
        lengthToPoints.put(10, 11);
        lengthToPoints.put(11, 11);
        lengthToPoints.put(12, 11);
        lengthToPoints.put(13, 11);
        lengthToPoints.put(14, 11);
        lengthToPoints.put(15, 11);
        lengthToPoints.put(16, 11);

        grid = new String[4][4];
        alreadyUsedWords = new ArrayList<>();
        currString = new StringBuilder();
        currPoints = 0;
        this.context = context;

        lastX = -999;
        lastY = -999;

    }

    public void addToPoints() {
        int length = currString.length();
        int points = lengthToPoints.get(length);
        currPoints += points;
    }

    public int scoreWord() {
        int length = currString.length();
        return lengthToPoints.get(length);
    }

    public int scoreWord(String word) {
        int length = word.length();
        return lengthToPoints.get(length);
    }

    public void reset() {
        grid = new String[4][4];
        alreadyUsedWords = new ArrayList<>();
        currString = new StringBuilder();
        currPoints = 0;
        lastX = -999;
        lastY = -999;
    }

    public void setUpGrid() {
        Random rand = new Random();
        int d, g1, g2;
        String letter;
        boolean spotFound;

        for (String die : boggleDice) {
            d = rand.nextInt(6);
            letter = die.substring(d, d+1);
            if (letter.equals("Q"))
                letter = "Qu";
            spotFound = false;
            do {
                g1 = rand.nextInt(4);
                g2 = rand.nextInt(4);
                if (grid[g1][g2] == null) {
                    grid[g1][g2] = letter;
                    spotFound = true;
                }
            } while (!spotFound);
        }
    }

    public void markWordAsUsed() {
        alreadyUsedWords.add(currString.toString().toLowerCase());
    }

    public boolean checkIfWordLongEnough() {
        if (currString.toString().length() >= 3)
            return true;
        else
            return false;
    }

    public boolean checkIfValidWord() {
        if (correctWords.contains(currString.toString().toLowerCase()))
            return true;
        else
            return false;
    }

    public boolean checkIfNewWord() {
        if (alreadyUsedWords.contains(currString.toString().toLowerCase()))
            return false;
        else
            return true;
    }

    public boolean checkIfValidTile(int x, int y) {
        if (lastX < 0 && lastY < 0)
            return true;
        else if (lastX - x == 1 && lastY - y == 1
                || lastX == x && lastY - y == 1
                || x - lastX == 1 && lastY - y == 1
                || lastX - x == 1 && lastY == y
                || x - lastX == 1 && lastY == y
                || lastX - x == 1 && y - lastY == 1
                || lastX == x && y - lastY == 1
                || x - lastX == 1 && y - lastY == 1)
            return true;
        else
            return false;
    }

    public void setLastSelectedTile(int newLastX, int newLastY) {
        lastX = newLastX;
        lastY = newLastY;
    }

    public String getLetter(int x, int y) {
        return grid[x-1][y-1];
    }

    public void addLetter(int x, int y) {
        currString.append(grid[x-1][y-1]);
    }

    public String getCurrWord() {
        return currString.toString().toLowerCase();
    }

    public void clearWord() {
        currString.setLength(0);
    }

    public void loadWordsFromFile() {
        correctWords = new ArrayList<>();
        try {
            InputStream in = context.getResources().openRawResource(R.raw.words_alpha);
            BufferedReader is = new BufferedReader(new InputStreamReader(in, "UTF8"));
            String line;
            do {
                line = is.readLine();
                correctWords.add(line);
            } while (line != null);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] getBoggleDice() {
        return boggleDice;
    }

    public HashMap<Integer, Integer> getLengthToPoints() {
        return lengthToPoints;
    }

    public void setLengthToPoints(HashMap<Integer, Integer> lengthToPoints) {
        this.lengthToPoints = lengthToPoints;
    }

    public String[][] getGrid() {
        return grid;
    }

    public void setUpGrid(String[][] grid) {
        this.grid = grid;
    }

    public List<String> getCorrectWords() {
        return correctWords;
    }

    public void setCorrectWords(List<String> correctWords) {
        this.correctWords = correctWords;
    }

    public List<String> getAlreadyUsedWords() {
        return alreadyUsedWords;
    }

    public void setAlreadyUsedWords(List<String> alreadyUsedWords) {
        this.alreadyUsedWords = alreadyUsedWords;
    }

    public StringBuilder getCurrString() {
        return currString;
    }

    public void setCurrString(StringBuilder currString) {
        this.currString = currString;
    }

    public int getCurrPoints() {
        return currPoints;
    }

    public void setCurrPoints(int currPoints) {
        this.currPoints = currPoints;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getLastX() {
        return lastX;
    }

    public void setLastX(int lastX) {
        this.lastX = lastX;
    }

    public int getLastY() {
        return lastY;
    }

    public void setLastY(int lastY) {
        this.lastY = lastY;
    }
}