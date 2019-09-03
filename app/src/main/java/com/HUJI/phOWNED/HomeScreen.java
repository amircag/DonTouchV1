package com.HUJI.phOWNED;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * HOME SCREEN ACTIVITY
 * This class contains the Main screen of the activity (called the "Home Screen"), the screen
 * that is displayed when the user opens the app, and is used to navigate to the user's profile,
 * groups, and the 'create new group' option.
 */
public class HomeScreen extends AppCompatActivity {

    /* Variables for user's groups */
    private ArrayList<GroupObj> groupsForUser = new ArrayList<>();
    private HashMap<String, Integer> userGroupIdMap = new HashMap<>();

    /* Main screen variables */
    private String userNickname;
    private String userPicUrl;
    private String userGamesCount;
    private String userOwnsCount;
    private String userTotalScore, userAvgScore;

    /* FIREBASE VARIABLES */
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference userDocument = db.collection("users").document(user.getUid());




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppDirectionLTR();
        loadScreen();

    }

    private void setAppDirectionLTR() {
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    /**
     * Sets up main screen content by calling other methods
     */
    private void loadScreen(){

        // Load user data from firebase:
        userDocument.get(Source.DEFAULT).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userNickname = documentSnapshot.getString("nickName");
                userPicUrl = documentSnapshot.getString("profilePic");

                userGamesCount = String.valueOf(documentSnapshot.get("myGamesCount"));
                userOwnsCount = String.valueOf(documentSnapshot.get("myOwnsCount"));
                userTotalScore = String.valueOf(documentSnapshot.get("myTotalScore"));

                // set default values in case user has no data yet:

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

                // Load user's groups data from firebase:
                db.collection("users").document(user.getUid())
                        .collection("teams")
                        .orderBy("name", Query.Direction.ASCENDING)
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> groupList = queryDocumentSnapshots.getDocuments();
                        int groupIndex = 0;
                        for (DocumentSnapshot group : groupList){
                            String groupName = group.getString("name");
                            String groupPic = group.getString("picUrl");
                            GroupObj groupObj = new GroupObj(groupName,groupPic,null,null);
                            groupObj.setGroupId(group.getId());
                            userGroupIdMap.put(group.getId(),groupIndex);
                            groupsForUser.add(groupObj);
                            groupIndex++;
                        }

                        findActiveGames();

                    }
                });
            }
        });
    }

    /**
     * Look in all of the user's groups to determine which of them have currently active games.
     * This is later used to display an "active game" icon on next to the group.
     */
    private void findActiveGames(){

        db.collection("games")
                .whereEqualTo("active",true)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> activeGames = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot game : activeGames){
                    String gameTeam = game.getString("teamId");
                    if (userGroupIdMap.containsKey(gameTeam)){
                        groupsForUser.get(userGroupIdMap.get(gameTeam)).setActiveGame(true);
                    }
                }

                displayScreen();

            }
        });
    }

    /**
     * Display screen contents with the data received from the server.
     */
    private void displayScreen(){
        setContentView(R.layout.activity_home_screen);

        /* Sets user box data */
        TextView nickname = findViewById(R.id.name);
        ImageView userProfilePictureHome = findViewById(R.id.MainProfilePicture);
        nickname.setText(userNickname);
        Glide.with(HomeScreen.this)
                .load(userPicUrl)
                .into(userProfilePictureHome);
        TextView userScore = findViewById(R.id.user_score_text);
        TextView userPhowns = findViewById(R.id.total_owns_counter);
        userScore.setText(userAvgScore+"%");


        // todo: unblock the next section to make color-changing score text
        /*
        int parsedScore = Integer.parseInt(userAvgScore);
        if (parsedScore >= 75){
            userScore.setTextColor(Color.GREEN);
        } else if (parsedScore >= 25){
            userScore.setTextColor(Color.YELLOW);
        } else {
            userScore.setTextColor(Color.RED);
        }
        */

        /* Display user phowns counter */
        userPhowns.setText(userOwnsCount);

        /* Sets group recycler data */
        initGroupRecyclerView();

        /* finally, set buttons */
        setButtonListeners();


    }


    /**
     * Start groups recycler view
     */
    private void initGroupRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        RecyclerView groupRecyclerView = findViewById(R.id.home_recycler_view);
        groupRecyclerView.setLayoutManager(layoutManager);
        /*HomeScreenRecyclerAdapter groupAdapter = new HomeScreenRecyclerAdapter(this,mGroupIds,mImageNames,mImages,mPeople, userNickname, userPicUrl);*/
        HomeScreenRecyclerAdapterUpdated groupAdapter = new HomeScreenRecyclerAdapterUpdated(this,groupsForUser,userNickname,userPicUrl);
        groupRecyclerView.setAdapter(groupAdapter);

        if(groupsForUser.size() != 0){
            TextView groupHint = findViewById(R.id.no_groups_hint);
            groupHint.setVisibility(View.INVISIBLE);
        }

    }


    /**
     * Sets all button actions for the screen
     */
    private void setButtonListeners(){
        /* Set "Go to Profile" listener */
        RelativeLayout userInfoBox = findViewById(R.id.Nickname);
        userInfoBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileScreen = new Intent(HomeScreen.this, ProfileScreen.class);
                profileScreen.putExtra("USER_ID",user.getUid());
                startActivity(profileScreen);

            }
        });
    }


    @Override
    public void onBackPressed() {
        // todo: unblock to add option of exit dialog
        /*new AlertDialog.Builder(this)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton("No",null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });*/
    }


    /**
     * go to "create group" screen
     * @param view current view
     */
    public void createGroupPressed(View view){
        Intent intent = new Intent(HomeScreen.this,AddMembersCreateGroup.class);
        intent.putExtra("USER_NICKNAME", userNickname);
        intent.putExtra("USER_PIC_URL", userPicUrl);
        startActivity(intent);
    }

}
