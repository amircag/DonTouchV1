package com.example.dontouchv1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "GroupRecyclerViewAdapter";

    //vars
    private ArrayList<String> mGroupNames = new ArrayList<>();
    private ArrayList<String> mGroupImages = new ArrayList<>();
    private ArrayList<String> mGroupMembers = new ArrayList<>();
    private Context mContext;

    public GroupRecyclerViewAdapter(Context mContext, ArrayList<String> mGroupNames, ArrayList<String> mGroupImages, ArrayList<String> mGroupMembers) {
        this.mGroupNames = mGroupNames;
        this.mGroupImages = mGroupImages;
        this.mGroupMembers = mGroupMembers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_circle_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG,"onBindViewHolder: called.");


        /*int id = mContext.getResources().
                getIdentifier("com.example.dontouchv1.R.drawable."+mGroupImages.get(position),
                        null,null);*/

        int imageId = mContext.getResources().getIdentifier
                ("drawable/"+mGroupImages.get(position),null,mContext.getPackageName());
        holder.image.setImageResource(imageId);

        //holder.image.setImageResource(R.drawable.group0);
        holder.name.setText(mGroupNames.get(position));
        holder.image.setOnClickListener(new View.OnClickListener(){
            @Override
                public void onClick(View view){
                Log.d(TAG,"OnClick: Clicked on an image: "+mGroupNames.get(position));
                Toast.makeText(mContext, mGroupMembers.get(position), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mGroupNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.groupImage);
            name =  itemView.findViewById(R.id.groupName);
        }
    }


}
