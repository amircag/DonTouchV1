package com.HUJI.phOWNED;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * GROUP'S PROFILE SCREEN
 * Used to display a group's info: name, image, identity of group members and last game's
 * winner & loser. From this screen the user can
 * (1) create a new game under the group
 * (2) go to another member's profile
 * (3) go to the "edit group" screen
 */
public class GroupProfileScreen extends AppCompatActivity {


    private ArrayList<String> mMembers = new ArrayList<>(15);
    private ArrayList<String> mImages = new ArrayList<>(15);
    private ArrayList<String> mMemberIds = new ArrayList<>(15);
    private DummyServer server = new DummyServer();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String teamId, teamPicUrl;
    private GroupObj thisGroup;
    private String name;
    private String lastGameId;
    private boolean isActiveGame;
    private boolean isNewGroup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        teamId = intent.getStringExtra("TEAM_ID");
        isActiveGame = intent.getBooleanExtra("GAME_ACTIVE",false);
        loadScreen(teamId);
    }

    /**
     * Listens for new games creates under the group. If a new game is created when the user
     * on this screen, the "New Game" button will change into a "Join Game"  button
     */
    private void listenNewGame(){
        DocumentReference team = db.collection("teams").document(teamId);
        ListenerRegistration listener = team.addSnapshotListener(GroupProfileScreen.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Private Error", "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Map<String,Object> data = snapshot.getData();

                    if(data.get("currentGame") != null && (lastGameId == null || lastGameId.isEmpty() || lastGameId.equals("null") || !((String)data.get("currentGame")).equals(lastGameId))){
                        lastGameId = (String) data.get("currentGame");

                        final Button gameButton = findViewById(R.id.gameButton);
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
                } else {
                    Log.d("Private Message", "Current data: null");
                }

            }
        });
    }

    /**
     * Loads group's data from the firebase firestore and the main screen components (by calling
     * other methods)
     * @param teamId the group to be displayed in the current instance.
     */
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
                if (firstPlaceId == null || lastPlaceId==null){
                    isNewGroup = true;
                }
                loadGroupMembers(teamId);
                listenNewGame();
            }
        });
    }

    /**
     * Load group members info from server to display under the "members" section
     * also loads the first/last place member's names and profile pics.
     * @param teamId the group to be displayed in the current instance.
     */
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
                    mMemberIds.add(member.getId());

                    // if a previous "first place" member already exists, set their details
                    if (member.getId().equals(thisGroup.getFirstPlaceId())){
                        thisGroup.setFirstPlaceName(memberName);
                        thisGroup.setFirstPlacePic(memberPic);
                    }

                    // if a previous "last place" member already exists, set their details
                    if (member.getId().equals(thisGroup.getLastPlaceId())){
                        thisGroup.setLastPlaceName(memberName);
                        thisGroup.setLastPlacePic(memberPic);
                    }
                }

                // load first and last place's data from the server
                if (!isNewGroup&&(thisGroup.getFirstPlaceName() == null || thisGroup.getFirstPlacePic() == null)) {
                    getFirstPlace();
                } else if (!isNewGroup&&(thisGroup.getLastPlaceName() == null || thisGroup.getLastPlacePic() == null)){
                    getLastPlace();
                } else {
                    startDisplay();
                }

            }
        });
    }

    /**
     * Get first place member's data from the server
     */
    private void getFirstPlace(){
        db.collection("users")
                .document(thisGroup.getFirstPlaceId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        thisGroup.setFirstPlacePic(documentSnapshot.getString("profilePic"));
                        thisGroup.setFirstPlaceName(documentSnapshot.getString("nickName"));

                        if (thisGroup.getLastPlaceName() == null || thisGroup.getLastPlacePic() == null){
                            getLastPlace();
                        } else {
                            startDisplay();
                        }
                    }
                });
    }

    /**
     * Get last place member's data from the server
     */
    private void getLastPlace(){
        db.collection("users")
                .document(thisGroup.getLastPlaceId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        thisGroup.setLastPlacePic(documentSnapshot.getString("profilePic"));
                        thisGroup.setLastPlaceName(documentSnapshot.getString("nickName"));
                        startDisplay();
                    }
                });
    }

    /**
     * call the layout and load all main screen components with data received from the server.
     */
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

        // call the method that initiates all buttons on screen
        initButtons();

        // set winner/loser image/name
        CircleImageView winnerImg = findViewById(R.id.group_winner_image);
        CircleImageView loserImg = findViewById(R.id.group_loser_image);
        TextView winnerTxt = findViewById(R.id.group_winner_name);
        TextView loserTxt = findViewById(R.id.group_loser_name);
        LinearLayout winLossContainer = findViewById(R.id.group_win_lose_container);
        TextView noGamesPlayed = findViewById(R.id.no_games_played_yet);
        if(!isNewGroup) {

            winnerTxt.setText(thisGroup.getFirstPlaceName());
            Glide.with(GroupProfileScreen.this)
                    .load(thisGroup.getFirstPlacePic())
                    .into(winnerImg);

            loserTxt.setText(thisGroup.getLastPlaceName());
            Glide.with(GroupProfileScreen.this)
                    .load(thisGroup.getLastPlacePic())
                    .into(loserImg);
        }else {
            winLossContainer.setVisibility(View.GONE);
            noGamesPlayed.setVisibility(View.GONE);
        }

        // set group members

        for (int i=0;i<mMembers.size();i++){
            mMemberIds.add("1");
        }
        initRecyclerGrid();

    }


    /**
     * Initiates the screen's member recycler grid view.
     */
    private void initRecyclerGrid(){
        GridLayoutManager gridLayout = new GridLayoutManager(this,4);
        RecyclerView membersRecycler = findViewById(R.id.groupMembersREC);
        membersRecycler.setLayoutManager(gridLayout);
        GroupMemberRecyclerAdapter myAdapter = new GroupMemberRecyclerAdapter(this,mMembers,mImages, mMemberIds);
        membersRecycler.setAdapter(myAdapter);
    }

    /**
     * Initiate all on screen buttons ("new game", "join game", "edit group" and back)
     */
    private void initButtons() {

        Button backButton = findViewById(R.id.groupBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final Button gameButton = findViewById(R.id.gameButton);

        // if there is a "currentGame" var for the group, check if game is active:

        // if no previous game ID, set button as "create game"
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
        } // if a previous game exists, set button as "join game"
        else if (isActiveGame){
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
            // if uncertain, check if game exists or not and set button accordingly
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

    @Override
    public void onBackPressed(){
        Intent homeScreen = new Intent(GroupProfileScreen.this,HomeScreen.class);
        startActivity(homeScreen);
    }

    /**
     * Start "edit group" activity
     * @param view curr view
     */
    public void editGroupPressed(View view){
        Intent editGroup = new Intent(this,EditGroup.class);
        editGroup.putExtra("TEAM_PIC",teamPicUrl);
        editGroup.putExtra("TEAM_ID",teamId);
        editGroup.putExtra("TEAM_NAME",name);
        startActivity(editGroup);
    }

}
