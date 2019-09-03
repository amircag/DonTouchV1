package com.HUJI.phOWNED;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * this class is responsible for the end game personal statistic and displaying them as a fragment
 * of the end game activity.
 * A simple {@link Fragment} subclass.
 */
public class EndGamePersonalStat extends Fragment {

    private TextView playerRank;
    private TextView playerScore;
    private TextView timeOnPhone;
    private TextView myName;
    private CircleImageView profileImage;
    private TextView ownCount;
    private int myScore, myRank,myOwnedCount;
    private long wastedTime;
    private String myPicUrl,myNickName,gameId;
    private LinearLayout toLogs;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public EndGamePersonalStat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get the data from the activity
        myScore = getArguments().getInt("MY_SCORE");
        myRank = getArguments().getInt("MY_RANK");
        myOwnedCount = getArguments().getInt("MY_OWMES_COUNT");
        myPicUrl = getArguments().getString("MY_PIC_URL");
        myNickName = getArguments().getString("MY_NICK_NAME");
        wastedTime  = getArguments().getLong("TIME_ON_PHONE");
        gameId = getArguments().getString("GAME_ID");

        // Inflate the layout for this fragment
        //View pr_lay = inflater.inflate(R.layout.fragment_end_game_personal_stat, container, false);
       return inflater.inflate(R.layout.fragment_end_game_personal_stat, container, false);
        //return pr_lay;
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        playerRank = view.findViewById(R.id.rankDynamic_ps);
        playerScore = view.findViewById(R.id.ScoreDynamic_ps);
        timeOnPhone = view.findViewById(R.id.timeDynamic_ps);
        profileImage = view.findViewById(R.id.personalImageForStats);
        ownCount  = view.findViewById(R.id.ownsDynamic_ps);
        myName = view.findViewById(R.id.player_name_for_stats);
        toLogs = view.findViewById(R.id.to_owns_log_bt);

        // go to lobby button
        Button lobbyButton = view.findViewById(R.id.lobby_bt);
        lobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToLobby = new Intent(getContext(),HomeScreen.class);
                startActivity(goToLobby);
            }
        });

        toLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToLogs = new Intent(getContext(),OwnedLog.class);
                goToLogs.putExtra("GAME_ID",gameId);
                goToLogs.putExtra("USER_ID",user.getUid());
                goToLogs.putExtra("FROM_GAME",true);
                startActivity(goToLogs);
            }
        });


        initPorfileImage();
        setPersonalStats();
    }

    /**
     * display the user pic
     */
    public void initPorfileImage(){
        Glide.with(getContext()).load(myPicUrl).disallowHardwareConfig().into(profileImage);

    }

    /**
     *  display to personal statistic
     */
    private void setPersonalStats(){
        playerRank.setText(String.valueOf(myRank));
        String score = String.valueOf(myScore)+"%";
        playerScore.setText(score);
        timeOnPhone.setText(setTimeOnPhone());
        myName.setText(myNickName);
        ownCount.setText(String.valueOf(myOwnedCount));
    }

    /**
     * convert millisecond to a hours:min:sec format
     * @return
     */
    private String setTimeOnPhone(){
        String seconds =String.valueOf((int) ((wastedTime / 1000) % 60)) ;
        String minutes =String.valueOf((int) ((wastedTime / (1000*60)) % 60));
        String hours   =String.valueOf((int) ((wastedTime / (1000*60*60)) % 24));
        if(seconds.length()==1){
            seconds = "0"+seconds;
        }
        if (minutes.length() == 1){
            minutes = "0"+minutes;
        }
        if (hours.length()==1){
            hours = "0"+hours;
        }
        return hours+":"+minutes+":"+seconds;
    }



}
