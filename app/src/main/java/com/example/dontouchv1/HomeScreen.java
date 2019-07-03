package com.example.dontouchv1;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class HomeScreen extends AppCompatActivity {


    private static final String TAG = "HomeScreen";
    private ArrayList<String> mGroupIds = new ArrayList<>(15);
    private ArrayList<String> mImageNames = new ArrayList<>(15);
    private ArrayList<String> mImages = new ArrayList<>(15);
    private ArrayList<String> mPeople = new ArrayList<>(15);
    private ArrayList<String> userData = new ArrayList<>();

    private ArrayList<GroupObj> groupsForUser = new ArrayList<>();
    private ArrayList<String> userGroupIds = new ArrayList<>();
    private HashMap<String, Integer> userGroupIdMap = new HashMap<>();

    private String userNickname;
    private String userPicUrl;
    private String userGamesCount;
    private String userOwnsCount;

    /* FIREBASE VARIABLES */
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference userDocument = db.collection("users").document(user.getUid());


    private DummyServer server = new DummyServer();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppDirectionLTR();
        loadScreen();

        /*loadServerData();*/

        /*setContentView(R.layout.activity_home_screen);

        initiateHomeScreen();
        *//*todo: change when groups exist *//*
        initGroupImages();
        setButtonListeners();*/

    }

    private void setAppDirectionLTR() {
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void loadScreen(){

        // LOAD USER DATA
        userDocument.get(Source.DEFAULT).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userNickname = documentSnapshot.getString("nickName");
                userPicUrl = documentSnapshot.getString("profilePic");
                userGamesCount = "9";
                userOwnsCount = "10";

                /*
                todo unblock after adding fields to server
                userOwnsCount = documentSnapshot.getString("myOwnsCount");
                userGameCount = documentSnapshot.getString("myGamesCount");
                 */

                // LOAD GROUP DATA
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


                        /*displayScreen();*/
                        findActiveGames();

                    }
                });
            }
        });
    }

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

    private void displayScreen(){
        setContentView(R.layout.activity_home_screen);

        /* Sets user box data */
        TextView nickname = findViewById(R.id.name);
        ImageView userProfilePictureHome = findViewById(R.id.MainProfilePicture);
        nickname.setText(userNickname);
        Glide.with(HomeScreen.this)
                .load(userPicUrl)
                .into(userProfilePictureHome);
        TextView userRating = findViewById(R.id.userrating);
        TextView timeOnPhone = findViewById(R.id.total_time_on_phone);
        userRating.setText(userOwnsCount);
        timeOnPhone.setText(userGamesCount);

        /* Sets group recycler data */
        initGroupRecyclerView();

        /* finally, set buttons */
        setButtonListeners();


    }

    private void loadServerData(){
        userDocument.get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    userNickname = document.getString("nickName");
                    userPicUrl = document.getString("profilePic");

                    setContentView(R.layout.activity_home_screen);

                    TextView nickname = findViewById(R.id.name);
                    ImageView userProfilePictureHome = findViewById(R.id.MainProfilePicture);

                    nickname.setText(userNickname);
                    Glide.with(HomeScreen.this)
                            .load(userPicUrl)
                            .into(userProfilePictureHome);

                }
                else{
                    setContentView(R.layout.activity_home_screen);
                }
                initiateHomeScreen();
                initGroupImages();
                setButtonListeners();
            }
        });
    }



    private void initGroupImages(){
        mGroupIds = server.getGroupIds();
        mImages = server.getGroupImages();
        mImageNames = server.getGroupNames();
        mPeople = server.getPeople();

        initGroupRecyclerView();
    }

    private void initiateHomeScreen(){

        /* Define data members */
        /*String tempNickname = "Fresh Prince";*/
        String tempRating = "34%";
        String timeOnPhoneText = "58 min";


        /* Define Views */
        /*TextView userNickname = findViewById(R.id.name);
        ImageView userProfilePictureHome = findViewById(R.id.MainProfilePicture);*/
        TextView userRating = findViewById(R.id.userrating);
        TextView timeOnPhone = findViewById(R.id.total_time_on_phone);
        ImageView TimeOnPhoneIm = findViewById(R.id.time_phone_icon);
        /*SearchView groupSearch = findViewById(R.id.searchView);
        groupSearch.onActionViewExpanded();
        groupSearch.setIconifiedByDefault(false);
        groupSearch.setQueryHint("Search Group...");
        if(!groupSearch.isFocused()){
            groupSearch.clearFocus();
        }
        groupSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/


        /* Grab user info from server */
        /*userNickname.setText(tempNickname);
        userProfilePictureHome.setImageResource(R.drawable.profile);*/

        /*if (userData.size() > 0){
            userNickname.setText(userData.get(0));
            Glide.with(HomeScreen.this)
                    .load(userData.get(1))
                    .into(userProfilePictureHome);
        }*/


        userRating.setText(tempRating);
        timeOnPhone.setText(timeOnPhoneText);
        //TimeOnPhoneIm.setImageResource(R.drawable.ontimesupport2);


    }

    private void initGroupRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        RecyclerView groupRecyclerView = findViewById(R.id.home_recycler_view);
        groupRecyclerView.setLayoutManager(layoutManager);
        /*HomeScreenRecyclerAdapter groupAdapter = new HomeScreenRecyclerAdapter(this,mGroupIds,mImageNames,mImages,mPeople, userNickname, userPicUrl);*/
        HomeScreenRecyclerAdapterUpdated groupAdapter = new HomeScreenRecyclerAdapterUpdated(this,groupsForUser,userNickname,userPicUrl);
        groupRecyclerView.setAdapter(groupAdapter);

    }



    private void setButtonListeners(){
        /* Set "Go to Profile" listener */
        RelativeLayout userInfoBox = findViewById(R.id.Nickname);
        userInfoBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileScreen = new Intent(HomeScreen.this, PersonalProfile.class);
                startActivity(profileScreen);

            }
        });
    }


    @Override
    public void onBackPressed() {
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

        // close when clicking back
        /*finish();*/
    }


    public void createGroupPressed(View view){
        Intent intent = new Intent(HomeScreen.this,AddMembersCreateGroup.class);
        intent.putExtra("USER_NICKNAME", userNickname);
        intent.putExtra("USER_PIC_URL", userPicUrl);
        startActivity(intent);
    }

}
