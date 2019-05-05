package com.example.dontouchv1;

import java.util.ArrayList;

public class DummyServer {

    /* GROUPS: Names, Images, Members */
    public ArrayList<String> groupNames = new ArrayList<>(15);
    public ArrayList<String> groupImages = new ArrayList<>(15);
    public ArrayList<String> groupMembersNames = new ArrayList<>(15);
    public ArrayList<String> groupMembersImages = new ArrayList<>(15);
    public ArrayList<String> people = new ArrayList<>(15);

    /* GAME: Rank, border, name, image */

    /*FAILS: Types (Images) + Mission*/
    public ArrayList<String> failMissions = new ArrayList<>(15);
    public ArrayList<String> failImages = new ArrayList<>(15);

    public DummyServer(){
        /* Demo Group */
        groupNames.add("The New Dream Team");
        groupImages.add("group0");
        people.add("Chef, Fresh Prince, NoaMen, AbuShefa & Liad");
        /*other groups*/
        groupNames.add("The Sopranos");
        groupImages.add("group1");
        people.add("Ted, Marshall, Lily, Robin & Kramer");
        groupNames.add("CS Faculty");
        groupImages.add("group2");
        people.add("Peter Parker, Beter Barker, Thomas Barker & Oded Shechter");
        groupNames.add("PPE Class '19");
        groupImages.add("group3");
        people.add("Benjamin, Moses, Gabriel & Jair");
        groupNames.add("Alcoholics Anonymous");
        groupImages.add("group4");
        people.add("155 Persons");
        groupNames.add("I&S Empire");
        groupImages.add("group5");
        people.add("Almog, Meital, Michal, Nethaniel, Maayan, Shpig, Shira & More");

        /* names and images */
        groupMembersNames.add("Chef");
        groupMembersImages.add("asaf");
        groupMembersNames.add("Fresh Prince");
        groupMembersImages.add("profile");
        groupMembersNames.add("NoaMen");
        groupMembersImages.add("noa");
        groupMembersNames.add("Abushefa");
        groupMembersImages.add("isar");
        groupMembersNames.add("Liad Alter");
        groupMembersImages.add("liav");

        /*init fails */
        failMissions.add("Buy Asaf a shot");
        failImages.add("beer");

        failMissions.add("Jump 20 times in front of the bartender");
        failImages.add("running");

        failMissions.add("Tell a story from your childhood");
        failImages.add("conversation");

        failMissions.add("Buy Liav a Breezer");
        failImages.add("beer");

        failMissions.add("Buy Nethaniel a Pizza");
        failImages.add("beer");

        failMissions.add("Talk about politics because everyone loves it");
        failImages.add("conversation");

        failMissions.add("Discuss your opinions of Twitter Bots");
        failImages.add("conversation");

        failMissions.add("Dance on the bar");
        failImages.add("running");

        failMissions.add("Drink 8 shots of Tequila");
        failImages.add("beer");
    }


    public ArrayList<String> getGroupNames(){
        return groupNames;
    }

    public ArrayList<String> getGroupImages() {
        return groupImages;
    }

    public ArrayList<String> getGroupMembersNames() {
        return groupMembersNames;
    }

    public ArrayList<String> getFailImages() {
        return failImages;
    }

    public ArrayList<String> getFailMissions() {
        return failMissions;
    }

    public ArrayList<String> getGroupMembersImages() {
        return groupMembersImages;
    }

    public ArrayList<String> getPeople() {
        return people;
    }
}
