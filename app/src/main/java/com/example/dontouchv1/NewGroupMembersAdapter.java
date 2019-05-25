package com.example.dontouchv1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewGroupMembersAdapter extends RecyclerView.Adapter<NewGroupMembersAdapter.ViewHolder> {

    private ArrayList<String> memberPic = new ArrayList<>();
    private ArrayList<String> membersName = new ArrayList<>();
    public ArrayList<String> memcersId = new ArrayList<>();
    public ArrayList<Android_Contact> contactsToAdd = new ArrayList<>();

    private Context mContaxt;

    public NewGroupMembersAdapter(ArrayList<Android_Contact> contacts,Context context) {
//        memberPic = memberImage;
//        membersName = name;
        mContaxt = context;
        this.contactsToAdd = contacts;
//        memcersId = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_members_recycleview,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public void removeFromView(int position){
//        memberPic.remove(position);
//        membersName.remove(position);
//        memcersId.remove(position);
        contactsToAdd.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final int position = i;
        viewHolder.newMemberName.setText(contactsToAdd.get(i).android_contact_Name);
        int imageId = mContaxt.getResources().getIdentifier("drawable/"+ contactsToAdd.get(i).android_contact_Name,null,mContaxt.getPackageName());
        viewHolder.newMemberPic.setImageResource(imageId);
        viewHolder.newMemberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 5/19/2019 remove from list
                removeFromView(position);
            }
        });
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

            newMemberPic = itemView.findViewById(R.id.newMemberPic);
            newMemberName = itemView.findViewById(R.id.newMemberName);
            newMemberLayout = itemView.findViewById(R.id.newMembersLayOut);

        }
    }


}
