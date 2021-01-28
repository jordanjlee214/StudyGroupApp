package com.leejordan.studygroupapp;

import java.util.HashMap;

public class RandomIDGenerator {

    public static String generate(){
        HashMap<Integer, String> numberToLetter = setUpLetterToNumberMap();
        int char1_number  = (int)(Math.random() * 10) + 1;
        int char2_number = (int)(Math.random() * 10) + 1;
        int char3_letter = (int)(Math.random() * 26) + 1;
        int char4_letter = (int)(Math.random() * 26) + 1;
        int char5_number = (int)(Math.random() * 10) + 1;
        int char6_letter = (int)(Math.random() * 26) + 1;
        return "" + (char1_number - 1) + (char2_number - 1) + numberToLetter.get(char3_letter) + numberToLetter.get(char4_letter) + (char5_number - 1) + numberToLetter.get(char6_letter);

    }

    public static HashMap<Integer, String> setUpLetterToNumberMap(){
        HashMap<Integer, String> numToLet = new HashMap<>();
        numToLet = new HashMap<>();
        numToLet.put(1, "a");
        numToLet.put(2, "b");
        numToLet.put(3, "c");
        numToLet.put(4, "d");
        numToLet.put(5, "e");
        numToLet.put(6, "f");
        numToLet.put(7, "g");
        numToLet.put(8, "h");
        numToLet.put(9, "i");
        numToLet.put(10, "j");
        numToLet.put(11, "k");
        numToLet.put(12, "l");
        numToLet.put(13, "m");
        numToLet.put(14, "n");
        numToLet.put(15, "o");
        numToLet.put(16, "p");
        numToLet.put(17, "q");
        numToLet.put(18, "r");
        numToLet.put(19, "s");
        numToLet.put(20, "t");
        numToLet.put(21, "u");
        numToLet.put(22, "v");
        numToLet.put(23, "w");
        numToLet.put(24, "x");
        numToLet.put(25, "y");
        numToLet.put(26, "z");
        return numToLet;
    }

}
