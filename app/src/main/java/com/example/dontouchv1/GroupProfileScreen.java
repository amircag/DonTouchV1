package com.example.dontouchv1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupProfileScreen extends AppCompatActivity {

    /* PHASE ONE: only includes:
        V Group Image
        V Name
        >>>> All group fails?? consider
        V Member List (As RecyclerView Grid?) + FAIL COUNTER
        V Create Game button


     */

    ArrayList<String> mMembers = new ArrayList<>();
    ArrayList<String> mImages = new ArrayList<>();
    ArrayList<String> mFailCounter = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile_screen);
        TextView groupTitle = findViewById(R.id.group_header_name);
        ImageView groupImage = findViewById(R.id.group_header_image);
        initGroupName(groupTitle);
        initGroupImage(groupImage);

        initMembers();


    }

    private void initMembers(){
        mMembers.add("amir");
        mImages.add("group0");
        mFailCounter.add("10");

        mMembers.add("Liav");
        mImages.add("group1");
        mFailCounter.add("5");

        mMembers.add("Roni");
        mImages.add("group2");
        mFailCounter.add("6");

        mMembers.add("ashga78ahf9asdasdasty53645");
        mImages.add("group3");
        mFailCounter.add("3");

        mMembers.add("Asaf");
        mImages.add("group4");
        mFailCounter.add("2");

        mMembers.add("ddd");
        mImages.add("group1");
        mFailCounter.add("65");

        mMembers.add("lo");
        mImages.add("group4");
        mFailCounter.add("14");

        mMembers.add("asaf");
        mImages.add("profile");
        mFailCounter.add("19");

        mMembers.add("do");
        mImages.add("group5");
        mFailCounter.add("6");

        mMembers.add("beer");
        mImages.add("beer");
        mFailCounter.add("4");

        initRecyclerGrid();
    }


    private void initRecyclerGrid(){
        GridLayoutManager gridLayout = new GridLayoutManager(this,4);
        RecyclerView membersRecycler = findViewById(R.id.groupMembersREC);
        membersRecycler.setLayoutManager(gridLayout);
        GroupMemberRecyclerAdapter myAdapter = new GroupMemberRecyclerAdapter(this,mMembers,mImages,mFailCounter);
        membersRecycler.setAdapter(myAdapter);


    }


    private void initGroupImage(ImageView groupImage){

        /* Temporary Data Members */
        String imageName = "group4";

        /* Set group header */
        int imageId = this.getResources().getIdentifier
                ("drawable/"+ imageName,null,this.getPackageName());
        groupImage.setImageResource(imageId);

    }

    private void initGroupName(TextView groupTitle){

        /* Temporary Data Members */
        String groupName = "The New Dream Team";

        /* Set group header */
        groupTitle.setText(groupName);

    }

}
