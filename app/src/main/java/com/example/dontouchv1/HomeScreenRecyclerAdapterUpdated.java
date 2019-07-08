package com.example.dontouchv1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeScreenRecyclerAdapterUpdated extends RecyclerView.Adapter<HomeScreenRecyclerAdapterUpdated.ViewHolder>{


    private static final String TAG = "HomeScreenRecAdapter";
    private String userNickname;
    private String userPicUrl;

    /*private ArrayList<String> mGroupIds, mImageNames, mImage, mPeople;*/
    private List<GroupObj> mGroups;
    private Context mContext;

    public HomeScreenRecyclerAdapterUpdated(Context mContext, List<GroupObj> mGroups, String userNickname, String userPicUrl) {
        this.mContext = mContext;
        this.userNickname = userNickname;
        this.userPicUrl = userPicUrl;
        this.mGroups = mGroups;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_group_layout, parent, false);
        return new HomeScreenRecyclerAdapterUpdated.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG,"onBindViewHolder: called.");
        /*int imageId = mContext.getResources().getIdentifier ("drawable/"+mImage.get(position),null,mContext.getPackageName());
        holder.image.setImageResource(imageId);*/

        Glide.with(mContext)
                .load(mGroups.get(position).getGroupPic())
                .disallowHardwareConfig()
                .into(holder.image);
        holder.groupName.setText(mGroups.get(position).getGroupName());

        if (mGroups.get(position).isActiveGame()){
            holder.activeGame.setVisibility(View.VISIBLE);
            holder.groupName.setTextColor(Color.parseColor("#008000"));
        }



        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {

                Intent groupPage = new Intent(mContext, GroupProfileScreen.class);
                groupPage.putExtra("TEAM_ID", mGroups.get(position).getGroupId());
                groupPage.putExtra("USER_NICKNAME", userNickname);
                groupPage.putExtra("USER_PIC_URL", userPicUrl);
                groupPage.putExtra("GAME_ACTIVE", mGroups.get(position).isActiveGame());
                mContext.startActivity(groupPage);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView groupName;
        LinearLayout parentLayout;
        ImageView activeGame;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            groupName = itemView.findViewById(R.id.image_name);
            parentLayout = itemView.findViewById(R.id.homeGroupCard);
            activeGame = itemView.findViewById(R.id.groupActiveGame);




        }
    }
}
