package com.HUJI.phOWNED;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for a Recycler View used in the Group Profile screen.
 */
public class GroupMemberRecyclerAdapter extends RecyclerView.Adapter<GroupMemberRecyclerAdapter.ViewHolder> {

    private static final String TAG = "GroupProfAdapter";

    // vars
    private ArrayList<String> mMemberNames = new ArrayList<>();
    private ArrayList<String> mMemberImages = new ArrayList<>();
    private ArrayList<String> mMemberIds = new ArrayList<>();
    private Context mContext;

    public GroupMemberRecyclerAdapter(Context mContext, ArrayList<String> mMemberNames, ArrayList<String> mMemberImages, ArrayList<String> mIds) {
        this.mMemberNames = mMemberNames;
        this.mMemberImages = mMemberImages;
        this.mMemberIds = mIds;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_member_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG,"onBindViewHolder: called.");

        Glide.with(mContext)
                .load(mMemberImages.get(position))
                .disallowHardwareConfig()
                .into(holder.image);
        holder.name.setText(mMemberNames.get(position));

        holder.image.setOnClickListener(new View.OnClickListener(){
            @Override
                public void onClick(View view){

                Intent userProfile = new Intent(mContext,ProfileScreen.class);
                userProfile.putExtra("USER_ID",mMemberIds.get(position));
                userProfile.putExtra("FROM_GROUP",true);
                mContext.startActivity(userProfile);


            }
        });

    }

    @Override
    public int getItemCount() {
        return mMemberNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.memberProfImage);
            name =  itemView.findViewById(R.id.memberName);
        }
    }


}
