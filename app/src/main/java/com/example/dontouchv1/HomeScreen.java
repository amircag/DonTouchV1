package com.example.dontouchv1;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {


    private static final String TAG = "HomeScreen";

    private ArrayList<String> mImageNames = new ArrayList<>(15);
    private ArrayList<String> mImages = new ArrayList<>(15);
    private ArrayList<String> mPeople = new ArrayList<>(15);
    private ArrayList<String> userData = new ArrayList<>();

    /* FIREBASE VARIABLES */
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference userDocument = db.collection("users").document(user.getUid());


    private DummyServer server = new DummyServer();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (getIntent().hasExtra("USER_DATA")){
            userData = getIntent().getStringArrayListExtra("USER_DATA");
        }*/


        loadServerData();

        /*setContentView(R.layout.activity_home_screen);

        initiateHomeScreen();
        *//*todo: change when groups exist *//*
        initGroupImages();
        setButtonListeners();*/



    }

    private void loadServerData(){
        userDocument.get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    String userNickname = document.getString("nickName");
                    String userProfilePic = document.getString("profilePic");

                    setContentView(R.layout.activity_home_screen);

                    TextView nickname = findViewById(R.id.name);
                    ImageView userProfilePictureHome = findViewById(R.id.MainProfilePicture);

                    nickname.setText(userNickname);
                    Glide.with(HomeScreen.this)
                            .load(userProfilePic)
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
        SearchView groupSearch = findViewById(R.id.searchView);
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
        });


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
        HomeScreenRecyclerAdapter groupAdapter = new HomeScreenRecyclerAdapter(this,mImageNames,mImages,mPeople);
        groupRecyclerView.setAdapter(groupAdapter);

    }

    private void moveToProfileScreen(){

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
        Intent intent = new Intent(this,AddMembersCreateGroup.class);
        startActivity(intent);
        finish();
    }

}
