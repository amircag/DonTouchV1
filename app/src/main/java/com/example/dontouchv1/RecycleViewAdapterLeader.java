package com.example.dontouchv1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleViewAdapterLeader extends RecyclerView.Adapter<RecycleViewAdapterLeader.ViewHelder>{
    private static final String TAG = "RecycleViewAdapterLeade";

    private ArrayList<LeaderBoardObj> leaderBoardObjs = new ArrayList<>();

    private Context lcontext;

    public RecycleViewAdapterLeader(ArrayList<LeaderBoardObj> leaders,Context context){
        leaderBoardObjs = leaders;
        lcontext = context;


    }
    @NonNull
    @Override
    public ViewHelder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.leaderboard_layout,viewGroup,false);
        ViewHelder holder = new ViewHelder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHelder holder, int i) {
        Log.d(TAG, "onBindViewHolder: ");

        Glide.with(lcontext)
                .load(leaderBoardObjs.get(i).getPicUrl())
                .disallowHardwareConfig()
                .into(holder.leaderBorardImange);
        int bgId = lcontext.getResources().getIdentifier ("drawable/"+leaderBoardObjs.get(i)
                .getBg(),null,lcontext.getPackageName());
        holder.boardBg.setBackgroundResource(bgId);
        holder.playerName.setText(leaderBoardObjs.get(i).getNickName());
        holder.playerRank.setText(String.valueOf(i+1));


    }

    @Override
    public int getItemCount() {
        return leaderBoardObjs.size();
    }

    public class ViewHelder extends RecyclerView.ViewHolder{
        CircleImageView leaderBorardImange;
        TextView playerRank;
        TextView playerName;
        RelativeLayout leaderBoardLayout;
        LinearLayout boardBg;

        public ViewHelder(@NonNull View itemView) {
            super(itemView);

            leaderBorardImange = itemView.findViewById(R.id.image_for_leaderboard);
            playerRank =itemView.findViewById(R.id.player_rank);
            playerName = itemView.findViewById(R.id.nameforLeaderboard);
            leaderBoardLayout = itemView.findViewById(R.id.reltive_layout_leaderBoard);
            boardBg = itemView.findViewById(R.id.player_bg);
        }
    }
}
