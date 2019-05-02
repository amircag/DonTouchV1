package com.example.dontouchv1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class GroupProfileScreen extends AppCompatActivity {

    /* PHASE ONE: only includes:
        > Group Image
        > Name
        > Member List (As RecyclerView Grid?)
        > Create Game button

     */

    ArrayList<String> mMembers = new ArrayList<>();
    ArrayList<String> mImages = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile_screen);

        initMembers();


    }

    private void initMembers(){
        mMembers.add("amir");
        mImages.add("group0");

        mMembers.add("Liav");
        mImages.add("group1");

        mMembers.add("Roni");
        mImages.add("group2");

        mMembers.add("amir");
        mImages.add("group3");

        mMembers.add("Asaf");
        mImages.add("group4");

        mMembers.add("ddd");
        mImages.add("group1");

        mMembers.add("lo");
        mImages.add("group4");

        mMembers.add("asaf");
        mImages.add("profile");

        mMembers.add("do");
        mImages.add("group5");

        mMembers.add("beer");
        mImages.add("beer");

        initRecyclerGrid();
    }


    private void initRecyclerGrid(){
        GridLayoutManager gridLayout = new GridLayoutManager(this,3);
        RecyclerView membersRecycler = findViewById(R.id.groupMembersREC);
        membersRecycler.setLayoutManager(gridLayout);
        GroupMemberRecyclerAdapter myAdapter = new GroupMemberRecyclerAdapter(this,mMembers,mImages);
        membersRecycler.setAdapter(myAdapter);


    }
}
