package com.example.dontouchv1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Random;

public class gameScreen extends AppCompatActivity {
    private TextView freindsInGameText;
    private TextView gameLog;
    private ScrollView scrolLog;
    private String eventLog = "";
    private String timeLog = "";
    private String showEventlog = "";
    private TextView timeGameLog;
    private Button endGameButton;
    private Handler fakeServer = new Handler();
    private int x = 0;
    final String[] lofMsg = {"Run 3 times", "Buy bear to every one",
            "talk with the next table", "Jump 4 times on one leg","buy s chaser",
            "order electric powder"};
    final String [] playersNames = {"Amir","Asaf","Noa","Issar"};





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamescreen);
        LinearLayout log_layout = findViewById(R.id.log_layout);
        freindsInGameText = findViewById(R.id.friendInGame);
        scrolLog = log_layout.findViewById(R.id.scroll_game_log);
        gameLog = log_layout.findViewById(R.id.popup_text_msg);
        timeGameLog = findViewById(R.id.popup_time);
        endGameButton=findViewById(R.id.end_game_button);

        playerInGameText();
        stratRep();
        endGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToStats = new Intent(gameScreen.this,EndGameStats.class);
                startActivity(goToStats);
            }
        });

    }

    /**
     * thid method create the popup
     * @param view the layout to present on
     * @param msg the massage to show
     */
    public void onButtonShowPopupWindowClick(final View view, final String msg) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);

        final View popupView = inflater.inflate(R.layout.popup_msg, null);


        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, 800, 800, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        fakeServer.postDelayed(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                TextView popupText = popupView.findViewById(R.id.popup_text);
                popupText.setText(msg);

            }
        },1000);

        fakeServer.postDelayed(new Runnable() {
            @Override
            public void run() {
                popupWindow.dismiss();
            }
        },7000);

    }


    /**
     * this method display the amount of friends in the game
     */
    private void playerInGameText(){

        String playersInGameText = getCurrentPlayers() + " / " + getTotalPlayers();
        freindsInGameText.setText(playersInGameText);
    }

    /**
     * this method display the game log and save it
     * @param event the event to add to the log as a String
     */
    private void setGameLogText(String event, String player){

        eventLog = getCurrentTime()+ "  " +player+" : "+event+ "\n" +"\n" + eventLog;
        showEventlog = player+" : "+event+ "\n" +"\n" + showEventlog;

        timeLog = getCurrentTime()+"\n"+getAmountOfLines(player+" : "+event)+timeLog;
        gameLog.setMovementMethod(new ScrollingMovementMethod());
        gameLog.setText(showEventlog);

        timeGameLog.setText(timeLog);

        timeGameLog.setMovementMethod(new ScrollingMovementMethod());

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

    private String getAmountOfLines(String event){
        String amountOfLines ="";
        int y = 1;
        while (event.length()*24 > 264*y){
            y++;
            amountOfLines += "\n";

        }
        return amountOfLines;
    }


    /*from here is the fake sever logic*/


    private void stratRep(){
        setFakeServe.run();
    }

    private Runnable setFakeServe = new Runnable() {

        @Override
        public void run() {
            if (x < lofMsg.length){

                fakeServer.postDelayed(this, 10000);
                severLogic(x);
                x++;
            }
        }
    };

    private void severLogic(final int indx){

        onButtonShowPopupWindowClick(findViewById(R.id.pop_up_layout),lofMsg[indx]);
        fakeServer.postDelayed(new Runnable() {
            @Override
            public void run() {
                setGameLogText(lofMsg[indx],getPlayerName());

            }
        },1500);
    }


    private String getPlayerName(){
        Random rd = new Random();
        int randomPlayer = rd.nextInt(4);
        return playersNames[randomPlayer];
    }

     private int getTotalPlayers(){
        return 10;
     }

     private int getCurrentPlayers(){
        return 3;
     }

    @Override
    public void onBackPressed() {
        //endGameButton()
    }

}

