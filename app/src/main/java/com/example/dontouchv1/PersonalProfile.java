package com.example.dontouchv1;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalProfile extends AppCompatActivity {

    private RecyclerView groupsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;


    private final String USER_NICKNAME = "NICKNAME";
    private final String USER_IMAGE = "PROFILE_IMAGE";

    /**
     * Firebase related data
     */
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DocumentReference userDocument = db.collection("users").document(user.getUid());


    // FAKE SERVER
    // todo: delete after Firebase Implementation
    private DummyServer server = new DummyServer();
    private ArrayList<String> mGroupNames = new ArrayList<>(15);
    private ArrayList<String> mGroupImages = new ArrayList<>(15);
    private ArrayList<String> mGroupMembers = new ArrayList<>(15);
    private ArrayList<String> mFailMissions = new ArrayList<>(15);
    private ArrayList<String> mFailImages = new ArrayList<>(15);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadProfileDetails();

        /*setContentView(R.layout.activity_personal_profile);
        // ^ add _gridcycle to change to grid view

        initGroupImages();
        initFails();
        grabStatistics();
        setBackButton();*/




    }


    /* --- GROUP RECYCLER METHODS --- */

    /**
     * Initializes Group images + names + members.
     * @// TODO: 30-Apr-19 grab actual data from server
     */
    private void initGroupImages(){

        mGroupImages = server.getGroupImages();
        mGroupNames = server.getGroupNames();
        mGroupMembers = server.getGroupMembersNames();

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

        mFailImages = server.getFailImages();
        mFailMissions = server.getFailMissions();

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
     * Init the user's profile image and username.
     */
    private void loadProfileDetails(){

        /* Load user data from server */
        userDocument.get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                /* after loading: */
                if (task.isSuccessful()) {

                    /*if loaded correctly, set image, nickname etc.*/

                    setContentView(R.layout.activity_personal_profile);
                    // ^ add _gridcycle to change to grid view

                    final CircleImageView profileImage = findViewById(R.id.profileImage);
                    final TextView userNickname = findViewById(R.id.nicknameDynamic);

                    DocumentSnapshot document = task.getResult();
                    String profileImageLink = document.getString("profilePic");
                    Glide.with(PersonalProfile.this)
                            .load(profileImageLink)
                            .into(profileImage);

                    String nickname = document.getString("nickName");
                    userNickname.setText(nickname);



                }
                else{
                    /*else just set the layout*/
                    setContentView(R.layout.activity_personal_profile);
                    // ^ add _gridcycle to change to grid view
                }

                /*to be replaced later with actual firebase functions*/
                initGroupImages();
                initFails();
                grabStatistics();
                setBackButton();
            }
        });

    }

    /**
     * Init the user's statistics, info etc..
     * @// TODO: 30-Apr-19 GRAB ACTUAL FROM SERVER
     */
    private void grabStatistics(){

        /* Temp. data members */
        String temp_rank = "1";
        String temp_time = "58 minutes";
        String temp_games_played = "12";
        String temp_fail_counter = "18";
        /* End of temp strings */

        /* Declare textViews */
        TextView rankText = findViewById(R.id.rankDynamic);
        TextView timeText = findViewById(R.id.timeDynamic);
        TextView gamesText = findViewById(R.id.gamesDynamic);
        TextView failsText = findViewById(R.id.failsDynamic);

        /* Set Text from Server / Temp */
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
                onBackPressed();
            }
        });
    }

}
