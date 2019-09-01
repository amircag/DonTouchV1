package com.HUJI.phOWNED;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class FailRecyclerViewAdapter extends RecyclerView.Adapter<FailRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "FailRecyclerViewAdapter";

    //vars
    private ArrayList<String> mFailImages = new ArrayList<>();
    private ArrayList<String> mFailMission = new ArrayList<>();
    private Context mContext;

    public FailRecyclerViewAdapter(Context mContext, ArrayList<String> mFailMission, ArrayList<String> mFailImages) {
        this.mFailImages = mFailImages;
        this.mFailMission = mFailMission;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fails_circle_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG,"onBindViewHolder: called.");

        int imageId = mContext.getResources().getIdentifier
                ("drawable/"+mFailImages.get(position),null,mContext.getPackageName());
        holder.image.setImageResource(imageId);

        holder.image.setOnClickListener(new View.OnClickListener(){
            @Override
                public void onClick(View view){
                Log.d(TAG,"OnClick: Clicked on an image: "+mFailImages.get(position));
                Toast.makeText(mContext, mFailMission.get(position), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFailImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.failImage);
        }
    }


}
