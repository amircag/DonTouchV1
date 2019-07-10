package com.example.dontouchv1;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class EndGamePersonalStat extends Fragment {

    private TextView playerRank;
    private TextView playerScore;
    private TextView timeOnPhone;
    private TextView myName;
    private CircleImageView profileImage;
    private ImageView beerOwned;
    private ImageView runOwned;
    private ImageView talkOwned;
    private String beerText ;
    private String runText ;
    private String talkText ;
    private int myScore, myRank,myOwnedCount;
    private long wastedTime;
    private String myPicUrl,myNickName;


    public EndGamePersonalStat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myScore = getArguments().getInt("MY_SCORE");
        myRank = getArguments().getInt("MY_RANK");
        myOwnedCount = getArguments().getInt("MY_OWMES_COUNT");
        myPicUrl = getArguments().getString("MY_PIC_URL");
        myNickName = getArguments().getString("MY_NICK_NAME");
        wastedTime  = getArguments().getLong("TIME_ON_PHONE");

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
        beerOwned = view.findViewById(R.id.beer_image_ps);
        runOwned = view.findViewById(R.id.run_image_ps);
        talkOwned = view.findViewById(R.id.conve_image_ps);
        myName = view.findViewById(R.id.player_name_for_stats);
        beerOwned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = (Toast) Toast.makeText(getContext(),getOwnedTextBeer(),Toast.LENGTH_SHORT);
                toast.setGravity(0,0,0);
                toast.show();
            }
        });

        talkOwned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getContext(),getOwnedTextTalk(),Toast.LENGTH_SHORT);
                toast.setGravity(0,0,0);
                toast.show();
            }
        });

        runOwned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getContext(),getOwnedTextRun(),Toast.LENGTH_SHORT);
                toast.setGravity(0,0,0);
                toast.show();
            }
        });

        Button lobbyButton = view.findViewById(R.id.lobby_bt);
        lobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToLobby = new Intent(getContext(),HomeScreen.class);
                startActivity(goToLobby);
            }
        });


        initPorfileImage();
        setPersonalStats();
    }

    public void initPorfileImage(){
        Glide.with(getContext()).load(myPicUrl).disallowHardwareConfig().into(profileImage);

    }

    private void setPersonalStats(){
        playerRank.setText(String.valueOf(myRank));
        playerScore.setText(String.valueOf(myScore));
        timeOnPhone.setText(setTimeOnPhone());
        myName.setText(myNickName);
    }

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


    private String getOwnedTextBeer(){
        beerText = "buy beer to Liad\n order the next round";
        return beerText;
    }

    private String getOwnedTextRun(){
        runText = "run 4 times around the table \n jump 4 times";
        return runText;
    }

    private String getOwnedTextTalk(){
        talkText = "you got lucky today \n No OWNED from this category";
        return talkText;
    }




}
