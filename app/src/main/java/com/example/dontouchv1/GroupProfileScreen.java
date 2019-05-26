package com.example.dontouchv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.security.acl.Group;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupProfileScreen extends AppCompatActivity {

    /* PHASE ONE: only includes:
        V Group Image
        V Name
        >>>> All group fails?? consider
        V Member List (As RecyclerView Grid?) + FAIL COUNTER
        V Create Game button


     */

    private ArrayList<String> mMembers = new ArrayList<>(15);
    private ArrayList<String> mImages = new ArrayList<>(15);
    private ArrayList<String> mFailCounter = new ArrayList<>(15);
    private DummyServer server = new DummyServer();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String teamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        teamId = intent.getStringExtra("teamId");

        setContentView(R.layout.activity_group_profile_screen);
        TextView groupTitle = findViewById(R.id.group_header_name);
        ImageView groupImage = findViewById(R.id.group_header_image);
        initGroupName(groupTitle);
        initGroupImage(groupImage);
        initButtons();
        initMembers();
        initWinners();


    }

    private void initMembers(){

        mMembers = server.getGroupMembersNames();
        mImages = server.getGroupMembersImages();

        mFailCounter.add("10");
        mFailCounter.add("12");
        mFailCounter.add("1");
        mFailCounter.add("77");
        mFailCounter.add("0");


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
        String imageName = "group0";

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

    private void initButtons() {

        Button createGroup = findViewById(R.id.newGameButton);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createGame = new Intent(GroupProfileScreen.this, NewSession.class);
                startActivity(createGame);
            }});


        Button backButton = findViewById(R.id.groupBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void initWinners(){
        CircleImageView winnerImg = findViewById(R.id.group_winner_image);
        CircleImageView loserImg = findViewById(R.id.group_loser_image);
        TextView winnerTxt = findViewById(R.id.group_winner_name);
        TextView loserTxt = findViewById(R.id.group_loser_name);

        winnerImg.setImageResource(R.drawable.asaf);
        winnerTxt.setText("Chef");

        loserImg.setImageResource(R.drawable.isar);
        loserTxt.setText("AbuShefa");


    }

}
