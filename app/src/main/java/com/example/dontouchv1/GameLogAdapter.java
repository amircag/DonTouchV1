package com.example.dontouchv1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GameLogAdapter extends RecyclerView.Adapter<GameLogAdapter.ViewHolder> {

    private ArrayList<GameLogObj> logs;


    private Context mContaxt;

    public GameLogAdapter(ArrayList<GameLogObj> logArray, Context context) {
//        memberPic = memberImage;
//        membersName = name;
        mContaxt = context;
        this.logs = logArray;
//        memcersId = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_game_screen,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public void removeFromView(int position){
//        memberPic.remove(position);
//        membersName.remove(position);
//        memcersId.remove(position);
        logs.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final int position = i;
        /*
        int imageId = mContaxt.getResources().getIdentifier("drawable/"+ logs.get(i).android_contact_Name,null,mContaxt.getPackageName());
        */
        Glide.with(mContaxt)
                .load(logs.get(position).getUserPicUrl())
                .disallowHardwareConfig()
                .into(viewHolder.userPic);

        int iconOwn = 0, ownType = logs.get(i).getOwnType();
        if (ownType == 1) iconOwn = R.drawable.own_1;
        else if (ownType == 2) iconOwn = R.drawable.own_2;
        else if (ownType == 3) iconOwn = R.drawable.own_3;

        //int picOwn = mContaxt.getResources().getIdentifier("drawable/"+logs.get(i).getOwnType(),null,mContaxt.getPackageName());
        viewHolder.ownType.setImageResource(iconOwn);
        final String ownDesc = logs.get(i).getOwnDesc();

        viewHolder.ownType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 5/19/2019 remove from list
                Toast toast = Toast.makeText(mContaxt,"Own Desciprtion", Toast.LENGTH_LONG);
                toast.setText(ownDesc);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setGravity(0,0,0);
                toast.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userPic;
        ImageView ownType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userPic = itemView.findViewById(R.id.userPic_game);
            ownType = itemView.findViewById(R.id.ownType_game);

        }
    }


}
