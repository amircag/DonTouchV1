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
import android.widget.LinearLayout;

import java.util.ArrayList;


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
        initLeaderBoard();
    }

    private void initLeaderBoard(){

        playersImage.add("group0");
        playersName.add("Asaf");
        playersRank.add("1.");
        playersBg.add("gold_lb");


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



}
