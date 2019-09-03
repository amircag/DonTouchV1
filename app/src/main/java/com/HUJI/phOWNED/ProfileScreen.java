package com.HUJI.phOWNED;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * PROFILE SCREEN
 * This activity creates a profile screen, either for the playing user or for other users of the app.
 * In the profile screen, users can see:
 * (1) User's name & image
 * (2) User's statistics (based on previous games played)
 * (3) User's groups
 * (4) Last owns received by the user
 * If the user visits their own profile, they also have the option of going to the
 * "Edit Profile" screen.
 */
public class ProfileScreen extends AppCompatActivity {

    /* Firebase variables */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    ProfileScreen self = this;

    /* data from Intent */
    private String userId;

    /* data from firestore */
    private String userPic, userName, userOwnsCount, userGamesCount, userWasteTime, userTotalScore;
    private String userAvgScore;
    final private ArrayList<GroupObj> usersGroups = new ArrayList<>();
    private String lastGameId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_profile_screen);*/
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");


        getUserData();
    }

    /**
     * Get the displayed user's data from firebase
     */
    private void getUserData(){

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        userName = documentSnapshot.getString("nickName");
                        userPic = documentSnapshot.getString("profilePic");
                        userOwnsCount = String.valueOf(documentSnapshot.get("myOwnsCount"));
                        userGamesCount = String.valueOf(documentSnapshot.get("myGamesCount"));
                        userTotalScore = String.valueOf(documentSnapshot.get("myTotalScore"));
                        userWasteTime = String.valueOf(documentSnapshot.get("wastedTime"));
                        lastGameId = documentSnapshot.getString("currentGame");

                        fillMissingData();
                        getGroupData();

                    }
                });
    }

    /**
     * Create placeholders when certain stats are missing (due to users being new)
     */
    private void fillMissingData(){

        if(userGamesCount.equals("null")){
            userOwnsCount = "0";
        }

        if(userWasteTime.equals("null")){
            userWasteTime = "0";
        }

        if (userGamesCount.equals("null")){
            userGamesCount = "0";
            userAvgScore = "100";
        }

        if (userOwnsCount.equals("null")){
            userOwnsCount = "0";
        }

        if (userTotalScore.equals("null")){
            userAvgScore = "100";
        } else {
            userAvgScore = String.valueOf(Integer.parseInt(userTotalScore) / Integer.parseInt(userGamesCount));
        }

    }


    /**
     * Get data for all of the displayed user's gorups.
     */
    private void getGroupData(){

        db.collection("users")
                .document(userId)
                .collection("teams")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> teams = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot team : teams){
                            String teamName = team.getString("name");
                            String teamPic = team.getString("picUrl");
                            usersGroups.add(new GroupObj(teamName,teamPic,null,null));
                        }


                        startView();


                    }
                });
    }

    /**
     * Initiates the screen.
     */
    private void startView(){
        setContentView(R.layout.activity_profile_screen);

        /* Set Image and text fields: */
        setScreenText();
        CircleImageView picture = findViewById(R.id.profilePageProfilePic);
        Glide
                .with(this)
                .load(userPic)
                .disallowHardwareConfig()
                .into(picture);

        /* if necessary, enable edit button */
        initEditButton();

        /* Finally display groups */
        initGroupRecyclerView();

        initLastGameData();
    }

    /**
     * Displays data about players' last game, and enables users to see that player's last game owns.
     */
    private void initLastGameData(){
        if (lastGameId == null){
            findViewById(R.id.lastGameHolder).setVisibility(View.GONE);
        } else {
            ImageView lastOwnsButton = findViewById(R.id.lastGameIcon);
            lastOwnsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent lastOwns = new Intent(self,OwnedLog.class);
                    lastOwns.putExtra("GAME_ID",lastGameId);
                    lastOwns.putExtra("USER_ID",userId);
                    lastOwns.putExtra("FROM_GAME",false);
                    startActivity(lastOwns);
                }
            });

            db.collection("games")
                    .document(lastGameId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            TextView lastGameTxt = findViewById(R.id.lastGameName);
                            String gameName = documentSnapshot.getString("name");
                            lastGameTxt.setText(gameName);

                        }
                    });

        }


    }

    /**
     * Initiates the edit button, if the user views their own profile.
     */
    private void initEditButton(){
        if (userId.equals(user.getUid())){

            Button editButton = findViewById(R.id.profileEdit);
            editButton.setVisibility(View.VISIBLE);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editIntent = new Intent(self,EditProfile.class);
                    editIntent.putExtra("NICKNAME",userName);
                    editIntent.putExtra("PROFILE_IMAGE",userPic);
                    startActivity(editIntent);
                    finish();
                }
            });


        }
    }

    /**
     * Sets most TextViews on the page with server data
     */
    private void setScreenText(){
        TextView ownsCount = findViewById(R.id.profileOwnsText);
        TextView gameCount = findViewById(R.id.profileGamesText);
        TextView score = findViewById(R.id.profileScoreText);
        TextView nameT = findViewById(R.id.profilePageName);
        ownsCount.setText(userOwnsCount);
        gameCount.setText(userGamesCount);
        score.setText(userAvgScore+"%");
        nameT.setText(userName);
        setTimerText();
    }

    /**
     * Calculates timestamp from epoch time and sets the relevant textview
     */
    private void setTimerText(){
        TextView wasteTime = findViewById(R.id.profileTimerText);
        String seconds = String.valueOf(((Integer.parseInt(userWasteTime) / 1000) % 60)) ;
        String minutes = String.valueOf(((Integer.parseInt(userWasteTime) / (1000*60)) % 60));
        String hours   = String.valueOf(((Integer.parseInt(userWasteTime) / (1000*60*60)) % 24));
        if(seconds.length()==1){
            seconds = "0" + seconds;
        }
        if (minutes.length() == 1){
            minutes = "0"+minutes;
        }
        if (hours.length()==1){
            hours = "0"+hours;
        }

        wasteTime.setText(hours+":"+minutes+":"+seconds);
    }

    /**
     * Starts Group recycler view, don't touch basically.
     */
    private void initGroupRecyclerView(){
        RecyclerView groupRecyclerView = findViewById(R.id.profileGroupRecycler);

        GridLayoutManager groupGridManager = new GridLayoutManager(this,4);
        groupRecyclerView.setLayoutManager(groupGridManager);

        ProfileRecyclerAdapter groupAdapter = new ProfileRecyclerAdapter(this,usersGroups);
        groupRecyclerView.setAdapter(groupAdapter);

    }


    /**
     * Calls action when back arrow button is pressed
     * @param view
     */
    public void backButtonPressed(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed(){
        if (getIntent().getBooleanExtra("FROM_GROUP",false)) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(this, HomeScreen.class);
            startActivity(intent);
            finish();
        }
    }


}
