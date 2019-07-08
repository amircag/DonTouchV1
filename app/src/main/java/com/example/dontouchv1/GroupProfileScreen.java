package com.example.dontouchv1;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupProfileScreen extends AppCompatActivity {


    private ArrayList<String> mMembers = new ArrayList<>(15);
    private ArrayList<String> mImages = new ArrayList<>(15);
    private ArrayList<String> mFailCounter = new ArrayList<>(15);
    private DummyServer server = new DummyServer();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String teamId, teamPicUrl;
    private GroupObj thisGroup;
    private String name;
    private String lastGameId;
    private boolean isActiveGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        teamId = intent.getStringExtra("TEAM_ID");
        isActiveGame = intent.getBooleanExtra("GAME_ACTIVE",false);
        loadScreen(teamId);

        /*setContentView(R.layout.activity_group_profile_screen);
        TextView groupTitle = findViewById(R.id.group_header_name);
        ImageView groupImage = findViewById(R.id.group_header_image);
        initGroupName(groupTitle,"a");
        initGroupImage(groupImage);
        initButtons();
        initMembers();
        initWinners();*/


    }

    private void loadScreen(final String teamId){
        final DocumentReference groupRef = db.collection("teams").document(teamId);
        groupRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DocumentSnapshot groupDoc = documentSnapshot;
                final String groupName = groupDoc.getString("name");
                final String groupPic = groupDoc.getString("picUrl");
                name = groupDoc.getString("name");
                teamPicUrl = groupDoc.getString("picUrl");
                lastGameId = groupDoc.getString("currentGame");
                final String firstPlaceId = groupDoc.getString("firstPlace");
                final String lastPlaceId = groupDoc.getString("lastPlace");
                thisGroup = new GroupObj(groupName,groupPic,firstPlaceId,lastPlaceId);

                loadGroupMembers(teamId);
            }
        });
    }

    private void loadGroupMembers(final String teamId){
        db.collection("teams").document(teamId)
                .collection("users")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> members = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot member : members){
                    String memberName = member.getString("nickName");
                    String memberPic = member.getString("picUrl");

                    mMembers.add(memberName);
                    mImages.add(memberPic);

                    if (member.getId().equals(thisGroup.getFirstPlaceId())){
                        thisGroup.setFirstPlaceName(memberName);
                        thisGroup.setFirstPlacePic(memberPic);
                    } else if (member.getId().equals(thisGroup.getLastPlaceId())){
                        thisGroup.setLastPlaceName(memberName);
                        thisGroup.setLastPlacePic(memberPic);
                    }
                }

                startDisplay();

            }
        });
    }

    private void startDisplay(){
        // set screen
        setContentView(R.layout.activity_group_profile_screen);

        // set group image + name
        TextView groupTitle = findViewById(R.id.group_header_name);
        groupTitle.setText(thisGroup.getGroupName());
        ImageView groupImage = findViewById(R.id.group_header_image);
        Glide.with(GroupProfileScreen.this)
                .load(thisGroup.getGroupPic())
                .centerCrop()
                .placeholder(R.drawable.spinner)
                .into(groupImage);

        initButtons(); // todo Update to button chooser Join / Start

        // set winner/loser
        CircleImageView winnerImg = findViewById(R.id.group_winner_image);
        CircleImageView loserImg = findViewById(R.id.group_loser_image);
        TextView winnerTxt = findViewById(R.id.group_winner_name);
        TextView loserTxt = findViewById(R.id.group_loser_name);

        winnerTxt.setText(thisGroup.getFirstPlaceName());
        Glide.with(GroupProfileScreen.this)
                .load(thisGroup.getFirstPlacePic())
                .into(winnerImg);

        loserTxt.setText(thisGroup.getLastPlaceName());
        Glide.with(GroupProfileScreen.this)
                .load(thisGroup.getLastPlacePic())
                .into(loserImg);

        // set group members

        // todo : remove when fail counter is removed
        for (int i=0;i<mMembers.size();i++){
            mFailCounter.add("1");
        }
        initRecyclerGrid();

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

        teamPicUrl = ((Integer)imageId).toString();
    }

    private void initGroupName(TextView groupTitle, String groupName){

        /* Temporary Data Members *//*
        String groupName = "The New Dream Team";*/

        /* Set group header */
        groupTitle.setText(groupName);

    }

    /*private void initButtons() {

        Button createGame = findViewById(R.id.newGameButton);
        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createGame = new Intent(GroupProfileScreen.this, NewSession.class);
                createGame.putExtra("TEAM_PIC_URL", teamPicUrl);
                createGame.putExtra("TEAM_NAME",name);
                createGame.putExtras(getIntent().getExtras());
//                createGame.putExtra("TEAM_ID", "SJvhAuMdny1mK0w2Ndvo");
                startActivity(createGame);
            }});

        Button joinGame = findViewById(R.id.joinGameButton);
        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: add a filter for team only
                CollectionReference games = db.collection("games");
                games.whereEqualTo("active", true)
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.getDocuments().size() == 0) return;

                                DocumentSnapshot game = queryDocumentSnapshots.getDocuments().get(0);
                                String gameId = (String) game.getId();

                                Intent joinGame = new Intent(GroupProfileScreen.this, GameScreen.class);
                                joinGame.putExtra("TEAM_PIC_URL", teamPicUrl);
                                joinGame.putExtra("GAME_ID", gameId);
                                joinGame.putExtra("TEAM_NAME",name);
                                joinGame.putExtras(getIntent().getExtras());
                                startActivity(joinGame);
                            }
                        });
            }});

        Button backButton = findViewById(R.id.groupBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }*/

    private void initButtons() {

        Button backButton = findViewById(R.id.groupBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final Button gameButton = findViewById(R.id.gameButton);

        // if there is a "currentGame" var, check if game is active
        if (lastGameId == null) {
            gameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent createGame = new Intent(GroupProfileScreen.this, NewSession.class);
                    createGame.putExtra("TEAM_PIC_URL", teamPicUrl);
                    createGame.putExtra("TEAM_NAME", name);
                    createGame.putExtras(getIntent().getExtras());
                    startActivity(createGame);
                }
            });
        } else if (isActiveGame){
            gameButton.setText("Join Game");
            gameButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_joingame));
            gameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent joinGame = new Intent(GroupProfileScreen.this, GameScreen.class);
                    joinGame.putExtra("TEAM_PIC_URL", teamPicUrl);
                    joinGame.putExtra("GAME_ID", lastGameId);
                    joinGame.putExtra("TEAM_NAME", name);
                    joinGame.putExtras(getIntent().getExtras());
                    startActivity(joinGame);
                }
            });
        } else {
            db.collection("games")
                    .document(lastGameId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.getBoolean("active")) {
                                gameButton.setText("Join Game");
                                gameButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_joingame));
                                gameButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent joinGame = new Intent(GroupProfileScreen.this, GameScreen.class);
                                        joinGame.putExtra("TEAM_PIC_URL", teamPicUrl);
                                        joinGame.putExtra("GAME_ID", lastGameId);
                                        joinGame.putExtra("TEAM_NAME", name);
                                        joinGame.putExtras(getIntent().getExtras());
                                        startActivity(joinGame);
                                    }
                                });
                            } else {
                                gameButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent createGame = new Intent(GroupProfileScreen.this, NewSession.class);
                                        createGame.putExtra("TEAM_PIC_URL", teamPicUrl);
                                        createGame.putExtra("TEAM_NAME", name);
                                        createGame.putExtras(getIntent().getExtras());
                                        startActivity(createGame);
                                    }
                                });
                            }
                        }
                    });
        }
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

    @Override
    public void onBackPressed(){
        Intent homeScreen = new Intent(GroupProfileScreen.this,HomeScreen.class);
        startActivity(homeScreen);
    }

}
