package com.example.dontouchv1;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.TooManyListenersException;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeScreenRecyclerAdapter extends RecyclerView.Adapter<HomeScreenRecyclerAdapter.ViewHolder>{


    private static final String TAG = "HomeScreenRecAdapter";
    private String userNickname;
    private String userPicUrl;

    private ArrayList<String> mGroupIds, mImageNames, mImage, mPeople;
    private Context mContext;

    public HomeScreenRecyclerAdapter(Context mContext, ArrayList<String> mGroupIds, ArrayList<String> mImageNames, ArrayList<String> mImage, ArrayList<String> mPeople, String userNickname, String userPicUrl) {
        this.mGroupIds = mGroupIds;
        this.mImageNames = mImageNames;
        this.mImage = mImage;
        this.mPeople = mPeople;
        this.mContext = mContext;
        this.userNickname = userNickname;
        this.userPicUrl = userPicUrl;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_group_layout, parent, false);
        return new HomeScreenRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG,"onBindViewHolder: called.");
        int imageId = mContext.getResources().getIdentifier ("drawable/"+mImage.get(position),null,mContext.getPackageName());
        holder.image.setImageResource(imageId);
        holder.imageName.setText(mImageNames.get(position));
        holder.people.setText(mPeople.get(position));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                Log.d(TAG,"OnClick: Clicked on an image: "+mImageNames.get(position));

                //CHANGE LATER TO ACTUAL ACTION!
                // Toast.makeText(mContext, "msg", Toast.LENGTH_SHORT).show();
                Intent groupPage = new Intent(mContext, GroupProfileScreen.class);
                groupPage.putExtra("TEAM_ID", mGroupIds.get(position));
                groupPage.putExtra("USER_NICKNAME", userNickname);
                groupPage.putExtra("USER_PIC_URL", userPicUrl);
                mContext.startActivity(groupPage);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView imageName;
        TextView people;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.image);
            imageName=itemView.findViewById(R.id.image_name);
            people=itemView.findViewById(R.id.people);
            parentLayout=itemView.findViewById(R.id.homeGroupCard);




        }
    }
}
