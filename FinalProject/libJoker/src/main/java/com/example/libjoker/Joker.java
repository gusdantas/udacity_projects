package com.example.libjoker;

import java.util.Random;

public class Joker {
    public static String getJoke(){
        Random rand = new Random();
        int jokeIndex = rand.nextInt(4) + 1;
        if (jokeIndex == 1){
            return "derp";
        } else if (jokeIndex == 2){
            return "herp";
        } else if (jokeIndex == 3){
            return "derpherp";
        } else {
            return "herpderp";
        }
    }
}
