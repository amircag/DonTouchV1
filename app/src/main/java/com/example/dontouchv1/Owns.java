package com.example.dontouchv1;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Owns {
    private final int BEER = 1, RUN = 2, CONVERS = 3;
    private List<Map.Entry<String, Integer>> owns;

    public Owns() {
        this.owns = new java.util.ArrayList<>();;
        this.owns.add(new AbstractMap.SimpleEntry<>("Jump 3 times", RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Buy everyone shots",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Talk with the next table",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Jump 99 times on one leg",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Call your mom",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Order electric powder",BEER));
    }

    public Map.Entry<String, Integer> getRandOwn(){
        Random rand = new Random();
        return owns.get(rand.nextInt(owns.size()));
    };
}
