package com.example.dontouchv1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameScreen extends AppCompatActivity {
    private String gameId, teamId, teamPicUrl, userNickname, userPicUrl;
    private int playersCount, ownsCount, myOwnsCount = 0;

    private Owns owns = new Owns();
    private int own_threshold;
    private Map.Entry<String, Integer> ownWarn;
    private boolean thrill = false;
    private int pauseCounter = 0;
    private boolean gameActive = true;

    private GameLogAdapter adapter;
    private ArrayList<GameLogObj> logArray = new ArrayList<>();

    private GameScreen self = this;
    ListenerRegistration gameListener;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.gamescreen);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        gameId = intent.getStringExtra("GAME_ID");
        teamId = intent.getStringExtra("TEAM_ID");
        teamPicUrl = intent.getStringExtra("TEAM_PIC_URL");
        int gameType = intent.getIntExtra("GAME_TYPE", -1);
        userNickname = intent.getStringExtra("USER_NICKNAME");
        userPicUrl = intent.getStringExtra("USER_PIC_URL");

        initHeader(intent);
        initLogsView();
        setTerminationButton();
        setGameType(gameType);

        gameListen();
        joinGame();
    }

    private void joinGame(){
        // Assuming player not leaves the game and come back later
        WriteBatch batch = db.batch();
        DocumentReference gameRf = db.collection("games").document(gameId);
        batch.update(gameRf, "playersCount", FieldValue.increment(1));

        DocumentReference userRf = db.collection("games").document(gameId).collection("players").document(user.getUid());
        Map<String,Object> userData = new HashMap<>();
        userData.put("joinAt", FieldValue.serverTimestamp());
        userData.put("userId",user.getUid());
        userData.put("userNickname", userNickname);
        userData.put("userPicUrl", userPicUrl);
        userData.put("myOwnsCount", (int) 0);
        batch.set(userRf, userData);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Private Error", "Failed to join a new game player");
            }
        });

    }

    private void gameListen(){
        final DocumentReference gameRf = db.collection("games").document(gameId);
        gameListener =  gameRf.addSnapshotListener(GameScreen.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Private Error", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Map<String,Object> data = snapshot.getData();
                    //update game
                    if((Boolean)data.get("active") == false){
                        gameActive = false;
                        nextToStat();
                        return;
                    }
                    if(((Long)data.get("playersCount")).intValue() != playersCount){
                        updatePlayersCounterText(((Long)data.get("playersCount")).intValue());
                    }
                    if(((Long)data.get("ownsCount")).intValue() != ownsCount){
                        gameRf.collection("owns").orderBy("createdAt", Query.Direction.DESCENDING).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList<GameLogObj> owns = new ArrayList<>();

                                for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                                    Map<String,Object> data = document.getData();
                                    String picUrl = (String) data.get("userPicUrl");
                                    String nickname = (String) data.get("userNickname");
                                    String ownDesc = (String) data.get("ownDesc");
                                    int ownType = ((Long) data.get("ownType")).intValue();
                                    GameLogObj own = new GameLogObj(picUrl,nickname,ownDesc,ownType);

                                    owns.add(own);
                                }
                                ownsCount = owns.size();

                                updateLogs(owns);
                            }
                        });
                    }
                    if (ownsCount == 0){
                        TextView marqueeText = findViewById(R.id.last_phowned);
                        Animation marquee = AnimationUtils.loadAnimation(self, R.anim.marquee);
                        marqueeText.startAnimation(marquee);
                    }
                } else {
                    Log.d("Private Message", "Current data: null");
                }
            }
        });

    }

    private void setGameType(int gameType) {
        if (gameType==R.id.chill) own_threshold = 5;
        else if (gameType==R.id.kill) own_threshold = 1;
        else if (gameType==R.id.thrill){
            thrill = true;
            updateThreshold();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(gameActive == false) return;

        pauseCounter++;
        if (pauseCounter % own_threshold == 0){
            myOwnsCount++;
            setOwn();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "phOWNED";
            String description = "got phOWNED";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("phOWNED", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setOwn() {
        final Map.Entry<String, Integer> own = owns.getRandOwn();

        WriteBatch batch = db.batch();

        DocumentReference ownRf = db.collection("games").document(gameId).collection("owns").document();
        Map<String,Object> ownData = new HashMap<>();
        ownData.put("ownDesc", own.getKey());
        ownData.put("ownType", own.getValue());
        ownData.put("userId",user.getUid());
        ownData.put("userNickname", userNickname);
        ownData.put("userPicUrl", userPicUrl);
        ownData.put("createdAt", FieldValue.serverTimestamp());
        batch.set(ownRf, ownData);

        DocumentReference player = db.collection("games").document(gameId).collection("players").document(user.getUid());
        batch.update(player,"myOwnsCount", myOwnsCount);

        DocumentReference game = db.collection("games").document(gameId);
        batch.update(game, "ownsCount", FieldValue.increment(1));

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                pushNotif(own);
                playSound();
                if(thrill) updateThreshold();
                ownWarn = own;
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void playSound() {
        MediaPlayer ring = MediaPlayer.create(GameScreen.this,R.raw.notif);
        ring.setLooping(false);
        ring.start();
    }

    private void pushNotif(Map.Entry<String, Integer> own) {
        createNotificationChannel();
        final Intent notificationIntent = new Intent(this, GameScreen.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "phOWNED")
                .setSmallIcon(R.drawable.phowned_logo_small)
                .setContentTitle("You Got phOWNED!")
                .setContentText(own.getKey())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(own.getKey()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setDefaults(0)
                .setSound(null);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameListen();
        if (ownWarn == null) return;
        showOwnPopup();
        ownWarn = null;
    }

    private void showOwnPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_own_popup, (ViewGroup) findViewById(R.id.gameOwnPopup), false);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        TextView popupText = (TextView) popupView.findViewById(R.id.ownPopupText);
        popupText.setText("You've got phOWNED!\n"+ ownWarn.getKey());

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(findViewById(R.id.recycler_game_log), Gravity.CENTER, 0, 0);


        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
        popupView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                popupWindow.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        // !!! not sure this update would take place
        DocumentReference gameRf = db.collection("games").document(gameId);
        gameRf.update("playersCount", FieldValue.increment(-1));
        playersCount--;
        if (playersCount == 0){
            gameRf.update("active",false);
        }
    }

    private void updateThreshold() {
        if (thrill){
            Random random = new Random();
            own_threshold = random.nextInt(5)+1;
        }
    }

    private void setTerminationButton() {
        ImageView endGameButton = findViewById(R.id.end_game_button);

        endGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(self)
                        .setTitle("Are you sure to Terminate?")
                        .setMessage("This will terminate the game for everybody!")
                        .setIcon(R.drawable.phowned_logo_small)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                terminateBack();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

    }

    private void updateLogs(ArrayList<GameLogObj> owns){
        logArray.clear();
        logArray.addAll(owns);
        GameLogObj lastOwn = logArray.get(0);
        updateMarquee(lastOwn);
        adapter.notifyDataSetChanged();
    }

    private void updateMarquee(GameLogObj own){
        TextView marqueeText = findViewById(R.id.last_phowned);
        marqueeText.setText(own.getUserName()+" got phOWNED - " + own.getOwnDesc());
        Animation marquee = AnimationUtils.loadAnimation(this, R.anim.marquee);
        marqueeText.startAnimation(marquee);
    }

    private void initLogsView(){
        RecyclerView logsView = findViewById(R.id.recycler_game_log);
        adapter = new GameLogAdapter(logArray,this);
        logsView.setAdapter(adapter);
        LinearLayoutManager linMng = new LinearLayoutManager(this);
        logsView.setLayoutManager(linMng);
    }

    private void terminateBack(){
        DocumentReference gameRf = db.collection("games").document(gameId);
        gameRf.update("active", false,
                "endedAt",FieldValue.serverTimestamp())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void nextToStat(){
        Intent goToStats = new Intent(GameScreen.this,EndGameStats.class);
        goToStats.putExtra("gameId", gameId);
        finish();
        startActivity(goToStats);
    }

    private void initHeader(Intent intent){
        String gameName = intent.getStringExtra("GAME_NAME");
        //String teamId = intent.getStringExtra("teamId");
        //Bitmap teamPic ;

        ((TextView)findViewById(R.id.gameNameGameScreen)).setText(gameName);

        Glide.with(this)
                .load(teamPicUrl)
                .disallowHardwareConfig()
                .into((ImageView)findViewById(R.id.teamPicGameScreen));
    }


    /**
     * this method display the amount of friends in the game
     */
    private void updatePlayersCounterText(int playersNum){
        TextView playersCountText = findViewById(R.id.playersCount);
        playersCountText.setText(Integer.toString(playersNum));
        playersCount = playersNum;
    }


    @Override
    public void onBackPressed() {
        //endGameButton()
    }

}

