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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owned_log);
        Intent intent = getIntent();
        gameId = intent.getStringExtra("GAME_ID");
        userId= intent.getStringExtra("USER_ID");
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

    public void initDisplay(){
        TextView ownsTitle = findViewById(R.id.ownsHeader);

        if (fromGame){
            ownsTitle.setText("MY OWNS");
        }
        else if (userId.equals(user.getUid())){
            ownsTitle.setText("MY LAST GAME\'S OWNS");
        } else {
            ownsTitle.setText("LAST GAME\'S OWNS");
        }


        if (ownLogObjs.size() == 0){
            System.out.println("no Owned");
            noOwnsPic.setVisibility(View.VISIBLE);
            noOwnsText.setVisibility(View.VISIBLE);
        }else{
            initRecycler();
        }
    }

    public void initRecycler() {
        RecyclerView recyclerView = findViewById(R.id.recycler_owns_log);
        OwnsLogAdapter adapter = new OwnsLogAdapter(ownLogObjs, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    public void backButtonPressed(View view){
        onBackPressed();
    }
}
