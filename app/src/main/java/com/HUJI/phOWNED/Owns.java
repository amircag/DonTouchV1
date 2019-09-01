package com.HUJI.phOWNED;

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
        this.owns.add(new AbstractMap.SimpleEntry<>("Jump 99 times on one leg",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Go to the middle of the bar and jump 15 times on one leg.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Stand on the table and belly dance.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Arm wrestle with a stranger.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Start a slow clap.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("You are the true lord of the dance. Now prove it.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Request the song eye of the tiger. Regardless, do 5 push-ups, run in place and punch the air.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Climb the table and sing “Baby Shark”.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Dance the “Macarena”.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Ask the bartender for a few cups of ice and start performing “Let It Go”.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Stand on a chair and do the FLOSS.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Get a stranger to come and sit at your table.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Do a headstand for 10 seconds.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Dance like a cowboy/cowgirl.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Start salsa dancing in the middle of the pub.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Wear your shirt inside out.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Put an ice cube down your shirt.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Do a model runway outside on the sidewalk.",RUN));
        this.owns.add(new AbstractMap.SimpleEntry<>("Buy everyone shots",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Buy a chaser to the person in front of you.",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Buy a beer to the person on your left.",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Drink a chaser.",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Buy a stranger a drink.",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Invite drinks to someone at another table.",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Get a stranger to buy you a drink.",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Buy two drinks and go over to a stranger and thank them.",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Try to get a free cake by saying its your birthday.",BEER)); // max Length
        this.owns.add(new AbstractMap.SimpleEntry<>("Drink a whole beer as fast as you can.",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Order the weirdest cocktail on the menu.",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Ask the bartender for a free drink.",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Order 2 different beer in the same glass.",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Buy a beer to the person on your right.",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Order french fries to the table.",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Order James Bond’s style martini",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Try to get free drinks for the table.",BEER));
        this.owns.add(new AbstractMap.SimpleEntry<>("Order electric powder",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Talk with the next table",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Call your mom",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Ask for the bartender number.",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Scream “ I love my mom “.",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Join a strangers table.",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Run to the nearest store, in a panic, and ask them if they’ve got a goldfish first aid kit.",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Ask a stranger to draw you.",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Ask a stranger for his autograph.",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Get a stranger to sing you “Happy Birthday”.",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Go to a stranger table and speak a made up language.",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Ask the bartender for his favorite color.",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Find a pen and ask a stranger to give you a “tattoo”.",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Wink at a stranger from across the room.",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Write a poem on a napkin and pass it to a stranger.",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Tell a stranger a convincing tale about your sun allergy.",CONVERS));
        this.owns.add(new AbstractMap.SimpleEntry<>("Tell a stranger that you come here often ",CONVERS));
    }

    public Map.Entry<String, Integer> getRandOwn(){
        Random rand = new Random();
        return owns.get(rand.nextInt(owns.size()));
    };
}
