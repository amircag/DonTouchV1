package com.example.dontouchv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {


    private static final String TAG = "HomeScreen";

    private ArrayList<String> mImageNames = new ArrayList<>(15);
    private ArrayList<String> mImages = new ArrayList<>(15);
    private ArrayList<String> mPeople = new ArrayList<>(15);
    private DummyServer server = new DummyServer();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        initGroupImages();
        initUserInfo();

        setButtonListeners();



    }


    private void initGroupImages(){

        mImages = server.getGroupImages();
        mImageNames = server.getGroupNames();
        mPeople = server.getPeople();

        /*mImages.add("group0");
        mImageNames.add("I&S Empire");
        mPeople.add("Chef, Michal, Maayan, Shpig, Shira, Nethaniel, Nick & You");

        mImages.add("group1");
        mImageNames.add("The LOVVVERS");
        mPeople.add("Oren Hazan, Donald Trump & You");

        mImages.add("group2");
        mImageNames.add("I<3BB");
        mPeople.add("1,140,369 Members & You");

        mImages.add("group3");
        mImageNames.add("PPE Class of 19'");
        mPeople.add("Gantzoosh, Ashkenazi, Lapid, Boogie & You");

        mImages.add("group4");
        mImageNames.add("New Dream Team");
        mPeople.add("Asaf, Amir, Issar, Noa & You");

        mImages.add("group5");
        mImageNames.add("LGBTQ+ community");
        mPeople.add("Liad & Amir 4 ever");*/

        initGroupRecyclerView();
    }

    private void initUserInfo(){

        /* Define data members */
        String tempNickname = "Fresh Prince";
        String tempRating = "34%";
        String timeOnPhoneText = "58 min";


        /* Define Views */
        TextView userNickname = findViewById(R.id.name);
        ImageView userProfilePictureHome = findViewById(R.id.MainProfilePicture);
        TextView userRating = findViewById(R.id.rate);
        TextView timeOnPhone = findViewById(R.id.timeonphone);
        ImageView TimeOnPhoneIm = findViewById(R.id.timeonphoneimg);
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
        userNickname.setText(tempNickname);
        userRating.setText(tempRating);
        userProfilePictureHome.setImageResource(R.drawable.profile);
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
        finish();
    }

}
