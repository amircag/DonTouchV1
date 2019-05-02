package com.example.dontouchv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

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
        // ^ add _gridcycle to change to grid view

        initGroupImages();
        initFails();
        initProfileImage();
        initProfileData();

        setBackButton();




    }


    /* --- GROUP RECYCLES METHODS --- */

    /**
     * Initialized Group images + names + members.
     * @// TODO: 30-Apr-19 grab actual data from server
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

    /**
     * Starts Group recycler view, don't touch basically.
     */
    private void initGroupRecyclerView(){
        RecyclerView groupRecyclerView = findViewById(R.id.groupRecyclerView);

        /* Switch comments to change to grid view: */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        groupRecyclerView.setLayoutManager(layoutManager);
        //GridLayoutManager groupGridManager = new GridLayoutManager(this,2);
        //groupRecyclerView.setLayoutManager(groupGridManager);
        /* Switch comments end */

        GroupRecyclerViewAdapter groupAdapter = new GroupRecyclerViewAdapter(this,mGroupNames,mGroupImages,mGroupMembers);
        groupRecyclerView.setAdapter(groupAdapter);

    }




    /* --- FAIL RECYCLES METHODS --- */

    /**
     * Initializes "Fails" images + information.
     * @// TODO: 30-Apr-19 grab actual data from server
     */
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

    /**
     * Starts //Fails// recycler view, don't touch basically.
     */
    private void initFailRecyclerView(){
        RecyclerView failsRecyclerView = findViewById(R.id.failsRecyclerView);

        /* Switch comments to change to Grid view: */
        LinearLayoutManager layoutManagerFails = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        failsRecyclerView.setLayoutManager(layoutManagerFails);
        //GridLayoutManager gridLayourFails = new GridLayoutManager(this,2);
        //failsRecyclerView.setLayoutManager(gridLayourFails);
        /* Switch comments end */

        FailRecyclerViewAdapter failAdapter = new FailRecyclerViewAdapter(this,mFailMissions,mFailImages);
        failsRecyclerView.setAdapter(failAdapter);

    }

    /**
     * Init the user's profile image.
     * @// TODO: 30-Apr-19 GRAB ACTUAL FROM SERVER
     */
    private void initProfileImage(){
        ;
        CircleImageView profileImage = findViewById(R.id.profileImage);
        profileImage.setImageResource(R.drawable.profile); // CHANGE THIS TO SERVER IMAGE
    }

    /**
     * Init the user's statistics, info etc..
     * @// TODO: 30-Apr-19 GRAB ACTUAL FROM SERVER
     */
    private void initProfileData(){

        /* Temp. data members */
        String temp_nick = "Fresh Prince of TLV";
        String temp_rank = "1";
        String temp_time = "58 minutes";
        String temp_games_played = "12";
        String temp_fail_counter = "18";
        /* End of temp strings */

        /* Declare textViews */
        TextView nicknameText = findViewById(R.id.nicknameDynamic);
        TextView rankText = findViewById(R.id.rankDynamic);
        TextView timeText = findViewById(R.id.timeDynamic);
        TextView gamesText = findViewById(R.id.gamesDynamic);
        TextView failsText = findViewById(R.id.failsDynamic);

        /* Set Text from Server / Temp */
        nicknameText.setText(temp_nick);
        rankText.setText(temp_rank);
        timeText.setText(temp_time);
        gamesText.setText(temp_games_played);
        failsText.setText(temp_fail_counter);





    }

    private void setBackButton(){

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeScreen = new Intent(PersonalProfile.this, HomeScreen.class);
                startActivity(homeScreen);

            }
        });
    }

}
