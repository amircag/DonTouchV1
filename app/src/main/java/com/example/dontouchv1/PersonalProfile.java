package com.example.dontouchv1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class PersonalProfile extends AppCompatActivity {

    private RecyclerView groupsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    // vars
    private ArrayList<String> mGroupNames = new ArrayList<>();
    private ArrayList<String> mGroupImages = new ArrayList<>();
    private ArrayList<String> mGroupMembers = new ArrayList<>();
    private ArrayList<String> mFailMissions = new ArrayList<>();
    private ArrayList<String> mFailImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);

        initGroupImages();
        initFails();




    }


    /* --- GROUP RECYCLES METHODS --- */

    /**
     * Later use to get Groups from Server
     */
    private void initGroupImages(){
        mGroupImages.add("group0");
        mGroupNames.add("I&S Empire");
        mGroupMembers.add("Chef, Michal, Maayan, Shpig, Shira, Nethaniel, Nick & You");

        mGroupImages.add("group1");
        mGroupNames.add("The LOVVVERS");
        mGroupMembers.add("Oren Hazan, Donald Trump & You");

        mGroupImages.add("group2");
        mGroupNames.add("I<3BB");
        mGroupMembers.add("1,140,369 Members & You");

        mGroupImages.add("group3");
        mGroupNames.add("PPE Class of 19'");
        mGroupMembers.add("Gantzoosh, Ashkenazi, Lapid, Boogie & You");

        mGroupImages.add("group4");
        mGroupNames.add("New Dream Team");
        mGroupMembers.add("Asaf, Amir, Issar, Noa & You");

        mGroupImages.add("group5");
        mGroupNames.add("LGBTQ+ community");
        mGroupMembers.add("Liad & Amir 4 ever");

        initGroupRecyclerView();
    }

    private void initGroupRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        RecyclerView groupRecyclerView = findViewById(R.id.groupRecyclerView);
        groupRecyclerView.setLayoutManager(layoutManager);
        GroupRecyclerViewAdapter groupAdapter = new GroupRecyclerViewAdapter(this,mGroupNames,mGroupImages,mGroupMembers);
        groupRecyclerView.setAdapter(groupAdapter);

    }




    /* --- FAIL RECYCLES METHODS --- */


    private void initFails(){
        mFailMissions.add("Buy Asaf a shot");
        mFailImages.add("beer");

        mFailMissions.add("Jump 20 times in front of the bartender");
        mFailImages.add("running");

        mFailMissions.add("Tell a story from your childhood");
        mFailImages.add("conversation");

        mFailMissions.add("Buy Liav a Breezer");
        mFailImages.add("beer");

        mFailMissions.add("Buy Nethaniel a Pizza");
        mFailImages.add("beer");

        mFailMissions.add("Talk about politics because everyone loves it");
        mFailImages.add("conversation");

        mFailMissions.add("Discuss your opinions of Twitter Bots");
        mFailImages.add("conversation");

        mFailMissions.add("Dance on the bar");
        mFailImages.add("running");

        mFailMissions.add("Drink 8 shots of Tequila");
        mFailImages.add("beer");


        initFailRecyclerView();
    }

    private void initFailRecyclerView(){
        LinearLayoutManager layoutManagerFails = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        RecyclerView failsRecyclerView = findViewById(R.id.failsRecyclerView);
        failsRecyclerView.setLayoutManager(layoutManagerFails);
        FailRecyclerViewAdapter failAdapter = new FailRecyclerViewAdapter(this,mFailMissions,mFailImages);
        failsRecyclerView.setAdapter(failAdapter);

    }


}
