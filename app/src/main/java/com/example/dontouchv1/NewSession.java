package com.example.dontouchv1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class NewSession extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private int selectedGame = R.id.thrill;
    private String teamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);

        Intent intent = getIntent();
        teamId = intent.getStringExtra("TEAM_ID");

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //((RadioGroup) findViewById(R.id.toggleGroup)).setOnCheckedChangeListener(ToggleListener);
    }

    static final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                LinearLayout lyout = (LinearLayout) radioGroup.getChildAt(j);
                for (int k = 0; k<lyout.getChildCount(); k++){
                    try{
                        final ToggleButton view = (ToggleButton) lyout.getChildAt(k);
                        view.setChecked(view.getId() == i);
                    } catch (Exception e){

                    }
                }
            }
        }
    };

    public void onToggle(View view) {
        ((RadioGroup)view.getParent().getParent()).check(view.getId());
        // app specific stuff ..
    }

    public void onTypeSelected(View view){
        LinearLayout linearLayout = (LinearLayout)view.getParent();
        for (int i=0; i<linearLayout.getChildCount(); i++){
            try{
                final LinearLayout button = (LinearLayout) linearLayout.getChildAt(i);
                button.setBackgroundResource(R.drawable.profile_border);
            } catch(Exception e){

            }
        }
        view.setBackgroundResource(R.drawable.profile_border_selected);

        // set the right description to arrive
        linearLayout = (LinearLayout) findViewById(R.id.games_desc);
        for (int i=0; i<linearLayout.getChildCount(); i++){
            try{
                final TextView desc = (TextView) linearLayout.getChildAt(i);
                desc.setVisibility(View.GONE);
            } catch (Exception e){

            }
        }
        switch (view.getId()){
            case R.id.chill:
                findViewById(R.id.chill_desc).setVisibility(View.VISIBLE);
                break;
            case R.id.thrill:
                findViewById(R.id.thrill_desc).setVisibility(View.VISIBLE);
                break;
            case R.id.kill:
                findViewById(R.id.kill_desc).setVisibility(View.VISIBLE);
                break;
            default:
        }

        //updates the selected game id var
         selectedGame = view.getId();
    }

    public void onClickCreate(View view){
        EditText gameNameText = (EditText) findViewById(R.id.gameName);
        //if(selectedGame == -1) return;
        final String gameName = gameNameText.getText().toString();
        if (gameName.trim().equals("")){
            gameNameText.setError("please write a game name");
            return;
        };

        HashMap<String,Object> game = new HashMap<>();
        game.put("name", gameName);
        game.put("type", selectedGame);
        game.put("teamId", teamId);
        game.put("creatorId", user.getUid());
        game.put("playersCount", (int) 0);
        game.put("ownsCount", (int) 0);
        game.put("active", true); /// to make sure db handles boolean
        db.collection("games").add(game).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    public void onSuccess(DocumentReference documentReference) {
                        String gameId = documentReference.getId();
                        next(gameName,gameId);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Private Error", "Failed to Create Game Document");
            }
        });
    }

    private void next(String name, String gameId) {
        Intent intent = new Intent(this, GameScreen.class);
        intent.putExtra("GAME_TYPE", selectedGame);
        intent.putExtra("GAME_NAME", name);
        intent.putExtra("GAME_ID", gameId);
        intent.putExtras(getIntent().getExtras()); //team_id,team_pic,user_nick,user_pic
        startActivity(intent);
    }
/*
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(NewSession.this, Statistics.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/
}
