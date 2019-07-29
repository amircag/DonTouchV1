/**
 * this class takes care of creating new game session
 * including game name and type
 */

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class NewSession extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private int selectedGame = R.id.thrill;
    private String teamId;
    private String teamPicUrl;

    /**
     * initiating screen
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);

        Intent intent = getIntent();
        teamId = intent.getStringExtra("TEAM_ID");
        teamPicUrl = intent.getStringExtra("TEAM_PIC_URL");
        //get an automatic random game name
        randomizeName();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //((RadioGroup) findViewById(R.id.toggleGroup)).setOnCheckedChangeListener(ToggleListener);
    }

    /**
     * selects game type (front)
     */
    static final RadioGroup.OnCheckedChangeListener ToggleListener =
            new RadioGroup.OnCheckedChangeListener() {
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

    /**
     * selecting game type (front)
     * @param view this view
     */
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

    /**
     * creates a new game in the db (with name and type)
     * and sends to next screen
     * @param view
     */
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
        game.put("teamPicUrl", teamPicUrl);
        game.put("creatorId", user.getUid());
        game.put("playersCount", (int) 0);
        game.put("ownsCount", (int) 0);
        game.put("active", true);
        game.put("createdAt", FieldValue.serverTimestamp());
        db.collection("games").add(game).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    public void onSuccess(DocumentReference documentReference) {
                        final String gameId = documentReference.getId();
                        DocumentReference teamRf =
                                db.collection("teams").document(teamId);
                        teamRf.update("currentGame", gameId,
                                "currentGameCreatedAt",
                                FieldValue.serverTimestamp())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                nextScreenGame(gameName, gameId);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Private Error", "Failed to Create Game Document");
            }
        });
    }

    /**
     * open the next screen - the Game Screen
     * @param name the game name
     * @param gameId the game id
     */
    private void nextScreenGame(String name, String gameId) {
        Intent intent = new Intent(this, GameScreen.class);
        intent.putExtra("GAME_TYPE", selectedGame);
        intent.putExtra("GAME_NAME", name);
        intent.putExtra("GAME_ID", gameId);
        intent.putExtras(getIntent().getExtras()); //team_id,team_pic,user_nick,user_pic
        startActivity(intent);
    }

    /**
     * when the random game name button pushed,
     * @param view this view
     */
    public void randomizePressed(View view){
        randomizeName();
    }

    /**
     * get a random game name
     */
    public void randomizeName(){
        ArrayList<String> names = new ArrayList<>();

        names.add("Beer & Fries");
        names.add("Binge Drinking");
        names.add("PARTY TIME!");
        names.add("Fish & Chips");
        names.add("Getting Stoopid");
        names.add("Shots");
        names.add("Day n Night");
        names.add("#WeStarted");
        names.add("Nights Out");
        names.add("Night to Forget");
        names.add("Night to Remember");
        names.add("Bar Nite");
        names.add("Cheers!");
        names.add("DonTouch");
        names.add("No Phones Allowed");

        EditText gameName = findViewById(R.id.gameName);

        Random rand = new Random();
        gameName.setText(names.get(rand.nextInt(names.size())));
    }

}
