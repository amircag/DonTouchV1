/**
* This is the Game Screen class
* It takes care of game architecture,
* real time data synchronization,
* and game behaviour for all players.
*/

package com.HUJI.phOWNED;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * this class is responsible for the game screen, this is the class that runs the game
 */
public class GameScreen extends AppCompatActivity {
    private String gameId, gameName, teamId, teamPicUrl, userNickname, userPicUrl, groupName;
    private int playersCount, ownsCount, myOwnsCount = 0;

    private Owns owns = new Owns();
    private int ownThreshold;
    private Map.Entry<String, Integer> ownWarn;
    private boolean thrill = false;
    private int pauseCounter = 0;
    private boolean gameActive = true;
    private Long myWasteTime = 0L;
    private Long startTime = SystemClock.elapsedRealtime();
    private Long startGame = SystemClock.elapsedRealtime();


    private GameLogAdapter adapter;
    private ArrayList<GameLogObj> logArray = new ArrayList<>();

    private GameScreen self = this;
    ListenerRegistration gameListener;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    /**
     * init a game
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set content view
        setContentView(R.layout.gamescreen);
        super.onCreate(savedInstanceState);

        //get game id, team and user basic info
        Intent intent = getIntent();
        gameId = intent.getStringExtra("GAME_ID");
        gameName = intent.getStringExtra("GAME_NAME");
        teamId = intent.getStringExtra("TEAM_ID");
        teamPicUrl = intent.getStringExtra("TEAM_PIC_URL");
        int gameType = intent.getIntExtra("GAME_TYPE", -1);
        userNickname = intent.getStringExtra("USER_NICKNAME");
        userPicUrl = intent.getStringExtra("USER_PIC_URL");
        groupName = intent.getStringExtra("TEAM_NAME");

        //init header part - game basic information
        initHeader();
        //init the real time players activity log
        initLogsView();
        setTerminationButton();
        setGameType(gameType);

        //listen to players activity changed in db
        gameListen();
        //join to a new/running game
        joinGame();
    }

    /*
    listen to players activity saved in db
    termination - will exit the game for everyone
    */
    private void gameListen(){
        final DocumentReference gameRf = db.collection("games").document(gameId);
        gameListener =  gameRf
                .addSnapshotListener(GameScreen.this, new EventListener<DocumentSnapshot>(){
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Private Error", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Map<String,Object> data = snapshot.getData();
                    // if game terminated by any user, then it exit this game screen
                    if((Boolean)data.get("active") == false && gameActive){
                        gameActive = false;
                        nextToStat();
                        return;
                    }

                    //init game data if need (return to game after exit)
                    if(gameName == null || !gameName.equals((String)data.get("name"))){
                        teamId = (String)data.get("teamId");
                        teamPicUrl = (String)data.get("teamPicUrl");
                        gameName = (String)data.get("name");
                        initHeader();
                        setGameType(((Long) data.get("type")).intValue());
                    }

                    //update game if any change has made

                    //new player joined
                    if(((Long)data.get("playersCount")).intValue() != playersCount){
                        updatePlayersCounterText(((Long)data.get("playersCount")).intValue());
                    }

                    // new OWN to a player need to be added to game log
                    if(((Long)data.get("ownsCount")).intValue() != ownsCount){
                        gameRf.collection("owns")
                                .orderBy("createdAt", Query.Direction.DESCENDING).get()
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
                                    GameLogObj own =
                                            new GameLogObj(picUrl,nickname,ownDesc,ownType);

                                    owns.add(own);
                                }
                                ownsCount = owns.size();

                                updateLogs(owns);
                            }
                        });
                    }
//                    if (ownsCount == 0){
//                        TextView marqueeText = findViewById(R.id.last_phowned);
//
//                        //unblock to animate
//                        /*Animation marquee = AnimationUtils.loadAnimation(self, R.anim.marquee);
//                        marqueeText.startAnimation(marquee);*/
//                    }
                } else {
                    Log.d("Private Message", "Current data: null");
                }
            }
        });

    }

    /**
     * join to the game (by db)
     */
    private void joinGame(){
        CollectionReference players = db.collection("games")
                .document(gameId).collection("players");
        players.whereEqualTo("userId", user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // if this player is not already joined the game, then update his data from db
                if(queryDocumentSnapshots.size() != 0){
                    DocumentSnapshot userData = queryDocumentSnapshots.getDocuments().get(0);
                    myOwnsCount = ((Long)userData.get("myOwnsCount")).intValue();
                    myWasteTime = (Long)userData.get("myWasteTime");
                    return;
                };


                WriteBatch batch = db.batch();
                DocumentReference gameRf = db.collection("games").document(gameId);
                batch.update(gameRf, "playersCount", FieldValue.increment(1));
                DocumentReference playerRf = db.collection("games").document(gameId)
                        .collection("players").document(user.getUid());
                Map<String,Object> userData = new HashMap<>();
                userData.put("joinAt", FieldValue.serverTimestamp());
                userData.put("userId",user.getUid());
                userData.put("userNickname", userNickname);
                userData.put("userPicUrl", userPicUrl);
                userData.put("myOwnsCount", (int) 0);
                userData.put("myWasteTime", (Long)0L);
                batch.set(playerRf, userData);

                DocumentReference userRf = db.collection("users").document(user.getUid());
                batch.update(userRf,"currentGame", gameId);

                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //updates player's games played counter
                        updateTotalGames();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Private Error", "Failed to join a new player to a game");
                    }
                });
            }
        });

    }


    /**
     * OWN threshold by game type
     * if "kill" it is by 1 time
     * if "chill" it is by 5 times
     * if "thrill" each player has a random 1-5 range
     * @param gameType the game type determined in NewSession or existing game
     */
    private void setGameType(int gameType) {
        if (gameType==R.id.chill) ownThreshold = 5;
        else if (gameType==R.id.kill) ownThreshold = 1;
        else if (gameType==R.id.thrill){
            thrill = true;
            updateThreshold();
        }
    }


    /**
     * tracking player touching the phone and getting out of game app
     * collect also time, and if crossed threshold it gets OWNED
     */
    @Override
    protected void onPause(){
        super.onPause();

        if (isScreenOn(this) && gameActive) {
            pauseCounter++;
            startTime = SystemClock.elapsedRealtime();
            if (pauseCounter % ownThreshold == 0) {
                myOwnsCount++;
                setOwn();
            }
        }
    }

    /**
     * Is the screen of the device on. (to find out the player didn't get out of game)
     * @param context the context of this screen
     * @return true when (at least one) screen is on
     */
    public boolean isScreenOn(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            System.out.println("checking display");
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            for (Display display : dm.getDisplays()) {
                if (display.getState() == Display.STATE_OFF){
                    return false;
                }
            }
            PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
            if (!pm.isInteractive()) return false;

            return true;
        } else {
            PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
            return pm.isScreenOn();
        }
    }

    /**
     * takes care of notification configuration in android
     */
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

    /**
     * set a new random Own to the player
     */
    private void setOwn() {
        // get a random own by Owns Class
        final Map.Entry<String, Integer> own = owns.getRandOwn();

        WriteBatch batch = db.batch();

        //write to owns log in db
        DocumentReference ownRf = db.collection("games")
                .document(gameId).collection("owns").document();
        Map<String,Object> ownData = new HashMap<>();
        ownData.put("ownDesc", own.getKey());
        ownData.put("ownType", own.getValue());
        ownData.put("userId",user.getUid());
        ownData.put("userNickname", userNickname);
        ownData.put("userPicUrl", userPicUrl);
        ownData.put("createdAt", FieldValue.serverTimestamp());
        batch.set(ownRf, ownData);

        //count owns to game player
        DocumentReference player = db.collection("games")
                .document(gameId).collection("players").document(user.getUid());
        batch.update(player,"myOwnsCount", myOwnsCount);

        //count owns of game
        DocumentReference game = db.collection("games").document(gameId);
        batch.update(game, "ownsCount", FieldValue.increment(1));

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                //notification to the OWNED player
                pushNotif(own);
                //updates to a new threshold if in "thrill" mode
                if(thrill) updateThreshold();
                ownWarn = own;
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    /**
     * push notification of OWN to the player that got OWNED
     * showing him OWN details and plays a noisy sound
     * @param own the own info
     */
    private void pushNotif(Map.Entry<String, Integer> own) {
        //createNotificationChannel();
        final Intent notificationIntent = new Intent(this, GameScreen.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = Uri.parse("android.resource://"
                + getApplicationContext().getPackageName()
                + "/" + R.raw.notif);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this,"phOWNED")
                .setSmallIcon(R.drawable.phowned_logo_small)
                .setContentTitle("You Got phOWNED!")
                .setContentText(own.getKey())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(own.getKey()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            if(soundUri != null){
                // Changing Default mode of notification
                builder.setDefaults(Notification.DEFAULT_VIBRATE);
                // Creating an Audio Attribute
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

                // Creating Channel
                NotificationChannel notificationChannel = new NotificationChannel(
                        "phOWNED","phOWNED",NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setSound(soundUri,audioAttributes);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    /**
     * takes care when OWNED player got back to game
     * collect waste time outside game screen
     * and shows him special popup of his OWN details
     */
    @Override
    protected void onResume(){
        super.onResume();
        //update wasteTime on server only when got phowned

        myWasteTime += (SystemClock.elapsedRealtime()-startTime);
        startTime = SystemClock.elapsedRealtime();
        db.collection("games").document(gameId).
                collection("players").document(user.getUid()).
                 update("myWasteTime",myWasteTime);
        gameListen();

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        if (ownWarn == null) return;
        showOwnPopup();
        ownWarn = null;
    }

    /**
     * show a popup to an OWNED player (when coming back to game screen)
     */
    private void showOwnPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(
                R.layout.game_own_popup,
                (ViewGroup)findViewById(R.id.gameOwnPopup),false);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        TextView popupText = (TextView) popupView.findViewById(R.id.ownPopupText);
        popupText.setText("You've got phOWNED!\n"+ ownWarn.getKey());

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(findViewById(R.id.recycler_game_log),Gravity.CENTER,0,0);


        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
        popupView.setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                popupWindow.dismiss();
            }
        });
    }

    /**
     * tries to find out if a player closed the application (from outside)
     */
    @Override
    protected void onDestroy(){
        // !!! not sure this update would take place
        DocumentReference gameRf = db.collection("games").document(gameId);
        gameRf.update("playersCount", FieldValue.increment(-1));
        playersCount--;
        if (playersCount == 0){
            gameRf.update("active",false);
        }
        super.onDestroy();
    }

    /**
     * get a random threshold in 1-5 range for "thrill" game type
     */
    private void updateThreshold() {
        if (thrill){
            Random random = new Random();
            ownThreshold = random.nextInt(5)+1;
        }
    }

    /**
     * termination button that close the game for all players
     * by updating the db of game finish
     */
    private void setTerminationButton() {
        ImageView endGameButton = findViewById(R.id.end_game_button);

        endGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(self)
                        .setTitle("Are you sure to Terminate?")
                        .setMessage("This will terminate the game for everybody!")
                        .setIcon(R.drawable.phowned_logo_small)
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                terminateBack();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }


    /**
     * updates the log view with given owns
     * @param owns owns array list to update
     */
    private void updateLogs(ArrayList<GameLogObj> owns){
        logArray.clear();
        logArray.addAll(owns);
        GameLogObj lastOwn = logArray.get(0);
        // if want to remove first own from recycle
        // logArray.remove(0);
        updateMarquee(lastOwn);
        adapter.notifyDataSetChanged();
    }

    /**
     * shows the marquee text of last own in the top of the log
     * @param own the last own obj
     */
    private void updateMarquee(GameLogObj own){

        CircleImageView lastOwnImg = findViewById(R.id.lastOwnedImg);
        Glide.with(this)
                .load(own.getUserPicUrl())
                .disallowHardwareConfig()
                .into(lastOwnImg);

        TextView lastOwnName = findViewById(R.id.lastOwnedName);
        lastOwnName.setText(own.getUserName());

        lastOwnImg.setVisibility(View.VISIBLE);
        lastOwnName.setVisibility(View.VISIBLE);

        TextView marqueeText = findViewById(R.id.last_phowned);
        /*marqueeText.setText(own.getUserName()+" got phOWNED - " + own.getOwnDesc());*/
        marqueeText.setText("phOWNED! "+own.getOwnDesc());

        // unblock to use marquee text
        /*Animation marquee = AnimationUtils.loadAnimation(this, R.anim.marquee);
        marqueeText.startAnimation(marquee);*/
    }

    /**
     *  initiating game log of owns
     */
    private void initLogsView(){
        RecyclerView logsView = findViewById(R.id.recycler_game_log);
        adapter = new GameLogAdapter(logArray,this);
        logsView.setAdapter(adapter);
        LinearLayoutManager linMng = new LinearLayoutManager(this);
        logsView.setLayoutManager(linMng);
    }


    /**
     * update db of game termination by one player
     * and saving the game length
     */
    private void terminateBack(){
        Long duration = SystemClock.elapsedRealtime() - startGame;
        DocumentReference gameRf = db.collection("games").document(gameId);
        gameRf.update("active", false,
                "endedAt",FieldValue.serverTimestamp(),"duration" , duration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    /**
     * updates player's games played counter
     */
    private void updateTotalGames(){
        DocumentReference userRAF = db.collection("users").document(user.getUid());
        WriteBatch batch = db.batch();
        batch.update(userRAF,"myGamesCount",FieldValue.increment(1));
    }

    /**
     * calls the next Statistics Screen
     */
    private void nextToStat(){
        Intent goToStats = new Intent(GameScreen.this,EndGameStats.class);
        goToStats.putExtra("TEAM_ID",teamId);
        goToStats.putExtra("TEAM_PIC_URL",teamPicUrl);
        goToStats.putExtra("GAME_ID", gameId);
        goToStats.putExtra("OWNS_COUNT", ownsCount);
        goToStats.putExtra("MY_OWNS_COUNT", myOwnsCount);
        goToStats.putExtra("MY_WASTE_TIME", myWasteTime);
        goToStats.putExtra("GAME_NAME",gameName);
        System.out.println("move to Stat");
        startActivity(goToStats);
        finish();
    }

    /**
     * initiating the header view of game general information
     */
    private void initHeader(){
        ((TextView)findViewById(R.id.gameNameGameScreen)).setText(gameName);

        Glide.with(this)
                .load(teamPicUrl)
                .disallowHardwareConfig()
                .into((ImageView)findViewById(R.id.teamPicGameScreen));

        CircleImageView teamPic = findViewById(R.id.teamPicGameScreen);
        teamPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("games")
                        .document(gameId)
                        .collection("players")
                        .orderBy("joinAt", Query.Direction.ASCENDING)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                String playerPrint = "";
                                List<DocumentSnapshot> players =
                                        queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot player : players){
                                    playerPrint = playerPrint + "\n" +
                                            player.getString("userNickname");
                                }

                                Toast toast = Toast.makeText(
                                        GameScreen.this,"text",Toast.LENGTH_SHORT);
                                toast.setGravity(0,0,0);
                                if (groupName != null){
                                    toast.setText(groupName+"\n\n Currently playing:"+playerPrint);
                                }
                                else {
                                    toast.setText("Currently playing:"+playerPrint);
                                }
                                toast.show();
                            }
                        });
            }
        });
    }


    /**
     * display num of players in the game
     */
    private void updatePlayersCounterText(int playersNum){
        TextView playersCountText = findViewById(R.id.playersCount);
        playersCountText.setText(Integer.toString(playersNum));
        playersCount = playersNum;
    }

    /**
     * blocks back button
     */
    @Override
    public void onBackPressed() {

    }

}

