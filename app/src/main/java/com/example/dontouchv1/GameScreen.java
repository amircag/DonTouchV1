package com.example.dontouchv1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.Distribution;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;

public class GameScreen extends AppCompatActivity {
    private String gameId;

    private int own_threshold;
    private boolean thrill = false;
    private int pauseCounter = 0;
    private Owns owns = new Owns();

    private TextView freindsInGameText;
    private LinearLayout scrolLog;
    private GameLogAdapter adapter;
    private ArrayList<GameLogObj> logArray = new ArrayList<>();

    final String [] playersNames = {"Fresh","Chef","NoaMe","AbuSh","Liad"};
    private GameScreen self = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setGameType();

        setContentView(R.layout.gamescreen);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //TODO: create game on server
        //gameId = ;
        updateHeader(intent);
        //TODO: listener to game termination
        initLogsView();
        TextView myTextView = findViewById(R.id.last_phowned);
        Animation marquee = AnimationUtils.loadAnimation(this, R.anim.marquee);
        myTextView.startAnimation(marquee);

        freindsInGameText = findViewById(R.id.friendInGame);
        scrolLog = findViewById(R.id.scroll_game_log);

        //TODO: update number of players
        playerInGameText();

        setTerminationButton();

    }

    private void setGameType() {
        Intent intent = getIntent();
        int gameType = intent.getIntExtra("GAME_TYPE", -1);
        if (gameType==R.id.chill) own_threshold = 5;
        else if (gameType==R.id.kill) own_threshold = 1;
        else if (gameType==R.id.thrill){
            thrill = true;
            updateThreshold();
        }
    }

    private void game(){

        if(thrill) updateThreshold();
    }

    @Override
    protected void onPause(){
        super.onPause();
        pauseCounter++;
        if (pauseCounter%own_threshold == 0){
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
        Map.Entry<String, Integer> own = owns.getRandOwn();
        //TODO:update server
        pushNotif(own);
        playSound();
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
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    @Override
    protected void onResume(){
        super.onResume();

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();

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
                                Intent goToStats = new Intent(GameScreen.this,EndGameStats.class);
                                goToStats.putExtra("gameId", gameId);
                                startActivity(goToStats);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

    }

    private void getLogs(){
        //create objects
        //logArray = new one
        GameLogObj lastOwn = logArray.get(logArray.size()-1);

        adapter.notifyDataSetChanged();
    }

    private void initLogsView(){
        RecyclerView logsView = findViewById(R.id.recycler_game_log);
        adapter = new GameLogAdapter(logArray,this);
        logsView.setAdapter(adapter);
        LinearLayoutManager linMng = new LinearLayoutManager(this);
        logsView.setLayoutManager(linMng);
    }

    private void terminateBack(){
        //TODO: back by boolean
    }

    private void updateHeader(Intent intent){
        String gameName = intent.getStringExtra("GAME_NAME");
        // TODO: when other screen are ready
        //String teamId = intent.getStringExtra("teamId");
        //Bitmap teamPic ;

        ((TextView)findViewById(R.id.gameNameGameScreen)).setText(gameName);
        //((ImageView)findViewById(R.id.teamPicGameScreen)).setImageBitmap(teamPic);
    }


    /**
     * this method display the amount of friends in the game
     */
    private void playerInGameText(){
        //TODO: realServer
        String playersInGameText = "5";
        freindsInGameText.setText(playersInGameText);
    }



    /**
     * return the current time
     */
    private String getCurrentTime(){
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int min = rightNow.get(Calendar.MINUTE);
        String time = hour +":"+ min;
        if(min<10){
            time = hour+ ":0"+min;
        }
        return time;
    }



    @Override
    public void onBackPressed() {
        //endGameButton()
    }

}

