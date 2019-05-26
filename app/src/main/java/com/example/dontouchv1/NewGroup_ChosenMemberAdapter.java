package com.example.dontouchv1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewGroup_ChosenMemberAdapter extends RecyclerView.Adapter<NewGroup_ChosenMemberAdapter.ViewHolder> {

    private ArrayList<String> memberPic = new ArrayList<>();
    private ArrayList<String> membersName = new ArrayList<>();
    public ArrayList<String> memcersId = new ArrayList<>();
    public ArrayList<Android_Contact> contactsToAdd = new ArrayList<>();

    private Context mContaxt;

    public NewGroup_ChosenMemberAdapter(ArrayList<Android_Contact> contacts, Context context) {
//
        mContaxt = context;
        this.contactsToAdd = contacts;
//
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_circle_view,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.newMemberName.setText(contactsToAdd.get(i).android_contact_Name);
        //int imageId = mContaxt.getResources().getIdentifier("drawable/"+ contactsToAdd.get(i).android_contact_Name,null,mContaxt.getPackageName());
        //viewHolder.newMemberPic.setImageResource(imageId);
        Glide.with(mContaxt).load(contactsToAdd.get(i).picUrl).into(viewHolder.newMemberPic);

    }

    @Override
    public int getItemCount() {
        return contactsToAdd.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView newMemberPic;
        TextView newMemberName;
        RelativeLayout newMemberLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newMemberPic = itemView.findViewById(R.id.groupImage);
            newMemberName = itemView.findViewById(R.id.groupName);
            newMemberLayout = itemView.findViewById(R.id.group_circle_layout);

        }
    }


}
