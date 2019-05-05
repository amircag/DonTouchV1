package com.example.dontouchv1;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class EndGamePersonalStat extends Fragment {

    private TextView playerRank;
    private TextView playerScore;
    private TextView timeOnPhone;
    private CircleImageView profileImage;
    private ImageView beerOwned;
    private ImageView runOwned;
    private ImageView talkOwned;
    private String beerText ;
    private String runText ;
    private String talkText ;


    public EndGamePersonalStat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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


        initPorfileImage();
        setPersonalStats();
    }

    public void initPorfileImage(){
        profileImage.setImageResource(R.drawable.profile);
    }

    private void setPersonalStats(){
        playerRank.setText(getPlayerRank());
        playerScore.setText(getPlayerScore());
        timeOnPhone.setText(getPlayerTimeOnPhone());
    }

    private String getPlayerScore(){
        return "23%";
    }

    private String getPlayerRank(){
        return "2";
    }

    private String getPlayerTimeOnPhone(){
        return "0:26";
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
