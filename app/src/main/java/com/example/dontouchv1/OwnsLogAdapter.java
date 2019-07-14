package com.example.dontouchv1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class OwnsLogAdapter extends RecyclerView.Adapter<OwnsLogAdapter.ViewHolder> {


    private LinearLayout onwLayout;
    private ArrayList<OwnLogObj> ownLogObjs = new ArrayList<>();
    private Context mContext;

    public OwnsLogAdapter(ArrayList<OwnLogObj> ownLogObjs , Context mContext){
        this.ownLogObjs = ownLogObjs;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.onws_log_recycler, onwLayout,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        int bgId = mContext.getResources().getIdentifier ("drawable/"+ownLogObjs.get(i)
                .getOwnPic(),null,mContext.getPackageName());
        viewHolder.ownPic.setImageResource(bgId);
        viewHolder.ownText.setText(ownLogObjs.get(i).getOwnDisc());
    }

    @Override
    public int getItemCount() {
        return ownLogObjs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView ownText;
        ImageView ownPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ownText = itemView.findViewById(R.id.own_text_log);
            ownPic = itemView.findViewById(R.id.onw_type_log);
            onwLayout = itemView.findViewById(R.id.own_log_layout);
        }
    }
}
