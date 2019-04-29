package com.example.dontouchv1;

import android.content.Context;
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


    private static final String TAG = "HomeScreenRecyclerAdapter";

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImage = new ArrayList<>();
    private ArrayList<String> mPeople = new ArrayList<>();
    private Context mContext;

    public HomeScreenRecyclerAdapter(Context mContext, ArrayList<String> mImageNames, ArrayList<String> mImage, ArrayList<String> mPeople) {
        this.mImageNames = mImageNames;
        this.mImage = mImage;
        this.mPeople = mPeople;
        this.mContext = mContext;
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
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                Log.d(TAG,"OnClick: Clicked on an image: "+mImageNames.get(position));
                Toast.makeText(mContext, "msg", Toast.LENGTH_SHORT).show();

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
            parentLayout=itemView.findViewById(R.id.parent_layout);




        }
    }
}
