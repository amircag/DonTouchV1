package com.HUJI.phOWNED;

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

/**
 * this class is a recycler adapter for adding members in creating new group screen
 */
public class NewGroupMembersAdapter extends RecyclerView.Adapter<NewGroupMembersAdapter.ViewHolder> {

    private ArrayList<String> memberPic = new ArrayList<>();
    private ArrayList<String> membersName = new ArrayList<>();
    public ArrayList<String> memcersId = new ArrayList<>();
    public ArrayList<Android_Contact> contactsToAdd = new ArrayList<>();

    private Context mContaxt;

    /**
     * constructor for the class
     */
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

    /**
     *insert the data to the holders
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final int position = i;
        viewHolder.newMemberName.setText(contactsToAdd.get(i).android_contact_Name);
        /*
        int imageId = mContaxt.getResources().getIdentifier("drawable/"+ contactsToAdd.get(i).android_contact_Name,null,mContaxt.getPackageName());
        */
        Glide.with(mContaxt)
                .load(contactsToAdd.get(position).picUrl)
                .disallowHardwareConfig()
                .into(viewHolder.newMemberPic);
        //remove from view and list when clicking
        viewHolder.newMemberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeFromView(position);
            }
        });
    }

    /**
     * @return the size of the recycler
     */
    @Override
    public int getItemCount() {
        return contactsToAdd.size();
    }

    /**
     * this class act as holder for the recycler objects
     */
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
