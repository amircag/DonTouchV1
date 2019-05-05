package com.example.dontouchv1;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class EndGameGroupStats extends Fragment {


    public EndGameGroupStats() {
        // Required empty public constructor
    }

    private ArrayList<String> playersName = new ArrayList<>();
    private ArrayList<String> playersRank = new ArrayList<>();
    private ArrayList<String> playersImage = new ArrayList<>();
    private ArrayList<String> playersBg = new ArrayList<>();
    private RecyclerView rv;
    private RecycleViewAdapterLeader adapter;
    private ImageView gorupImage;
    private CircleImageView winnerImage;
    private CircleImageView loserImage;
    private TextView groupName;
    private TextView groupScore;
    private TextView groupFails;
    private TextView groupDuration;
    private TextView winnerName;
    private TextView loserName;
    private TextView meetUpName;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_end_game_group_stats, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv=(RecyclerView)view.findViewById(R.id.recycle_leaderBoard);
        gorupImage = view.findViewById(R.id.GroupImageForStats);
        winnerImage = view.findViewById(R.id.WinnerImageForStats);
        loserImage = view.findViewById(R.id.loserImageForStats);
        groupName = view.findViewById(R.id.group_name_for_stats);
        groupScore = view.findViewById(R.id.ScoreDynamic);
        groupFails = view.findViewById(R.id.failsDynamicEndScreen);
        groupDuration = view.findViewById(R.id.timeDynamic);
        winnerName = view.findViewById(R.id.winner_name_for_stats);
        loserName = view.findViewById(R.id.Loser_name_for_stats);
        meetUpName = view.findViewById(R.id.GameNameDynamic);
        initGroupImage();
        setGroupStast();
        initLeaderBoard();
        setWinnerLoserDisplay();
    }

    private void initLeaderBoard(){

        playersImage.add("group0");
        playersName.add("Asaf");
        playersRank.add("1.");
        playersBg.add("gold_bg");


        playersImage.add("group1");
        playersName.add("Amir");
        playersRank.add("2.");
        playersBg.add("silver_bl");



        playersImage.add("group2");
        playersName.add("Noa");
        playersRank.add("3.");
        playersBg.add("bronze_bl");


        playersImage.add("group3");
        playersName.add("Issar");
        playersRank.add("4.");
        playersBg.add("profile_border");


        playersImage.add("group4");
        playersName.add("Liad");
        playersRank.add("5.");
        playersBg.add("profile_border");


        playersImage.add("group5");
        playersName.add("AbuShefa");
        playersRank.add("6.");
        playersBg.add("red_bl");


        setuoData();

    }

    private void setuoData(){
        adapter = new RecycleViewAdapterLeader(playersImage,playersName,playersRank,getContext(),playersBg);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void initGroupImage(){
        gorupImage.setImageResource(R.drawable.group0);
    }

    private void setWinnerLoserDisplay(){
        winnerImage.setImageResource(R.drawable.group5);
        loserImage.setImageResource(R.drawable.group4);
        winnerName.setText("Asaf");
        loserName.setText("AbuShefe");
    }

    private void setGroupStast(){
        groupName.setText(getGroupName());
        groupScore.setText(getGroupScore());
        groupFails.setText(getGroupOwned());
        groupDuration.setText(getGroupDuration());
        meetUpName.setText(getGameName());
    }

    private String getGroupName(){
        return "Dream Team";
    }

    private String getGroupScore(){
        return "34%";
    }

    private String getGroupOwned(){
        return "13";
    }

    private String getGroupDuration(){
        return "1:57";
    }

    private String getGameName(){
        return "The Good Guys";
    }




}
