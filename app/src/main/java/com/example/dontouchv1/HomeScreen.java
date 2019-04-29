package com.example.dontouchv1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {


    private static final String TAG = "HomeScreen";

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mPeople = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);

        initGroupImages();

    }


    private void initGroupImages(){
        mImages.add("group0");
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
        mPeople.add("Liad & Amir 4 ever");

        initGroupRecyclerView();
    }

    private void initGroupRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        RecyclerView groupRecyclerView = findViewById(R.id.groupRecyclerView);
        groupRecyclerView.setLayoutManager(layoutManager);
        GroupRecyclerViewAdapter groupAdapter = new GroupRecyclerViewAdapter(this,mImageNames,mImages,mPeople);
        groupRecyclerView.setAdapter(groupAdapter);

    }

}
