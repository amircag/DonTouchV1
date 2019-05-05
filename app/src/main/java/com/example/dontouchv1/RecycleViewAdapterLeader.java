package com.example.dontouchv1;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleViewAdapterLeader extends RecyclerView.Adapter<RecycleViewAdapterLeader.ViewHelder>{
    private static final String TAG = "RecycleViewAdapterLeade";

    private ArrayList<String> playerImages = new ArrayList<>();
    private ArrayList<String> playerNmaes = new ArrayList<>();
    private ArrayList<String> playerRank = new ArrayList<>();
    private ArrayList<String> imageBg = new ArrayList<>();
    private Context lcontext;

    public RecycleViewAdapterLeader(ArrayList<String> imageName, ArrayList<String> playerNmae,ArrayList<String> rank,Context context,ArrayList<String> bg){
        playerImages = imageName;
        playerNmaes = playerNmae;
        playerRank = rank;
        lcontext = context;
        imageBg = bg;

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
        int imageId = lcontext.getResources().getIdentifier ("drawable/"+playerImages.get(i),null,lcontext.getPackageName());
        holder.leaderBorardImange.setImageResource(imageId);
        int bgId = lcontext.getResources().getIdentifier ("drawable/"+imageBg.get(i),null,lcontext.getPackageName());
        holder.boardBg.setBackgroundResource(bgId);
        holder.playerName.setText(playerNmaes.get(i));
        holder.playerRank.setText(playerRank.get(i));


    }

    @Override
    public int getItemCount() {
        return playerImages.size();
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
