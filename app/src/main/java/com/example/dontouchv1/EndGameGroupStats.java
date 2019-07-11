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
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class EndGameGroupStats extends Fragment {


    public EndGameGroupStats() {
        // Required empty public constructor
    }

    private ArrayList<LeaderBoardObj> leaderBoardObjs = new ArrayList<>();
    private String duration,gameName,teamPicUrl,teamName;
    private int ownesCount;

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
        leaderBoardObjs = (ArrayList<LeaderBoardObj>)getArguments().getSerializable("LEADER_BOARD");
        duration = getArguments().getString("DURATION");
        ownesCount = getArguments().getInt("OWNES_COUNT");
        gameName = getArguments().getString("GAME_NAME");
        teamPicUrl = getArguments().getString("TEAM_PIC_URL");
        teamName = getArguments().getString("TEAM_NAME");
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

//
//
//        playersImage.add("asaf");
//        playersName.add("Chef");
//        playersRank.add("1.");
//        playersBg.add("gold_lb");
//
//
//        playersImage.add("amir");
//        playersName.add("Fresh Prince");
//        playersRank.add("2.");
//        playersBg.add("silver_bl");
//
//
//
//        playersImage.add("noa");
//        playersName.add("NoaMen");
//        playersRank.add("3.");
//        playersBg.add("bronze_bl");
//
//
//        playersImage.add("isar");
//        playersName.add("AbuShefa");
//        playersRank.add("4.");
//        playersBg.add("profile_border");
//
//
//        playersImage.add("liav");
//        playersName.add("Liad");
//        playersRank.add("5.");
//        playersBg.add("red_bl");


        setupData();

    }

    private void setupData(){
        colorFirstAndSecond();
        adapter = new RecycleViewAdapterLeader(leaderBoardObjs,getContext());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

    }
    private void colorFirstAndSecond(){
        leaderBoardObjs.get(0).setBg("gold_lb");
        if (leaderBoardObjs.size()>1) {
            leaderBoardObjs.get(1).setBg("silver_bl");
        }
        if (leaderBoardObjs.size()>2){
            leaderBoardObjs.get(leaderBoardObjs.size()-1).setBg("red_bl");
        }
    }

    private void initGroupImage(){
        Glide.with(getContext())
                .load(teamPicUrl)
                .disallowHardwareConfig()
                .into(gorupImage);
    }

    private void setWinnerLoserDisplay(){
        String winnerPic = leaderBoardObjs.get(0).getPicUrl();
        String winnerNickName = leaderBoardObjs.get(0).getNickName();
        String losserPic = leaderBoardObjs.get(leaderBoardObjs.size()-1).getPicUrl();
        String losserName = leaderBoardObjs.get(leaderBoardObjs.size()-1).getNickName();
        Glide.with(getContext()).load(winnerPic).disallowHardwareConfig().into(winnerImage);
        Glide.with(getContext()).load(losserPic).disallowHardwareConfig().into(loserImage);
        winnerName.setText(winnerNickName);
        loserName.setText(losserName);
    }

    private void setGroupStast(){
        groupName.setText(teamName);

        groupFails.setText(String.valueOf(ownesCount));
        groupDuration.setText(duration);
        meetUpName.setText(gameName);
    }

    private String getGroupName(){
        return "The New Dream Team";
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
