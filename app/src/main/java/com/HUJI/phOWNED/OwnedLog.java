package com.HUJI.phOWNED;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * this class is responsible for the OWNED LOG screen. this class will display the player last game
 * owns in a recyclerView. the player could be the user (then showing is OWNS from the last game)
 * or a friend (then showing the friend's OWNS from last game)
 */
public class OwnedLog extends AppCompatActivity {

    private String gameId;
    private ArrayList<OwnLogObj> ownLogObjs = new ArrayList<>();
    private TextView noOwnsText;
    private ImageView noOwnsPic;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Button toLobby;
    private Context self = this;
    private String userId;
    private boolean fromGame;

    /**
     * invoke when the screen is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owned_log);
        Intent intent = getIntent();
        gameId = intent.getStringExtra("GAME_ID");
        userId= intent.getStringExtra("USER_ID");
        //boolean ver determined if the user came to the screen from the end game statistic or not
        fromGame = intent.getBooleanExtra("FROM_GAME",false);
        noOwnsText = findViewById(R.id.no_owns_text);
        noOwnsPic = findViewById(R.id.no_owns_ic);
        noOwnsText.setVisibility(View.INVISIBLE);
        noOwnsPic.setVisibility(View.INVISIBLE);
        toLobby = findViewById(R.id.lobby_bt_own_log);
        toLobby.setVisibility(View.INVISIBLE);
        if (fromGame){
            toLobby.setVisibility(View.VISIBLE);
        }
        toLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToLobby = new Intent(self,HomeScreen.class);
                startActivity(goToLobby);
            }
        });

        getOwns();




    }

    /**
     * this method retract the owns from the database for displaying them
     */
    public void getOwns(){
        db.collection("games").document(gameId).collection("owns").whereEqualTo("userId",userId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> owns = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot own : owns){
                    if (own.get("userId").equals(userId)){
                        OwnLogObj ownLog = new OwnLogObj(own.getString("ownDesc")
                                ,(Long)own.get("ownType"));
                        ownLogObjs.add(ownLog);
                        System.out.println("owned found!");
                    }
                }
                initDisplay();
            }
        });

    }

    /**
     * this method initial the display of the screen. the title will change according to the screen
     * the user came from( 1. from end game statistic 2. from his own profile
     * 3. from a friend profile). the method will display OWNS log if the player had OWNS in the
     * last game or congrats pic if not
     */
    public void initDisplay(){
        TextView ownsTitle = findViewById(R.id.ownsHeader);
        //came form the end game screen
        if (fromGame){
            ownsTitle.setText("MY OWNS");
        }
        //came from the user profile
        else if (userId.equals(user.getUid())){
            ownsTitle.setText("MY LAST GAME\'S OWNS");
        }
        //came from a friend profile
        else {
            ownsTitle.setText("LAST GAME\'S OWNS");
        }

        //which kind of display (OWNS or congrats pic)
        if (ownLogObjs.size() == 0){
            System.out.println("no Owned");
            noOwnsPic.setVisibility(View.VISIBLE);
            noOwnsText.setVisibility(View.VISIBLE);
        }else{
            initRecycler();
        }
    }

    /**
     * this method initial the OWNS recycler view
     */
    public void initRecycler() {
        RecyclerView recyclerView = findViewById(R.id.recycler_owns_log);
        OwnsLogAdapter adapter = new OwnsLogAdapter(ownLogObjs, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //enabling the back button
    public void backButtonPressed(View view){
        onBackPressed();
    }
}
