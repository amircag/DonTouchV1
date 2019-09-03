/**
 * this class is adapter for the game log of owns
 */
package com.HUJI.phOWNED;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * this class is a recycler adapter for the game log screen
 */
public class GameLogAdapter extends RecyclerView.Adapter<GameLogAdapter.ViewHolder> {

    private ArrayList<GameLogObj> logs;
    private Context mContaxt;

    /**
     * initiate the adapter
     * @param logArray array list of GameLogObj with owns info
     * @param context the context of the main GameScreen
     */
    public GameLogAdapter(ArrayList<GameLogObj> logArray, Context context) {
        mContaxt = context;
        this.logs = logArray;
    }

    /**
     * inflates the view with the logs
     * @param viewGroup
     * @param i
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.recycle_game_screen,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    /**
     * remove own from specific index
     * @param position the index to remove
     */
    public void removeFromView(int position){
        logs.remove(position);
        notifyDataSetChanged();
    }

    /**
     * arranging own info to be inflated to the vies
     * @param viewHolder
     * @param i
     */
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
        final String ownedUser = logs.get(i).getUserName();

        //onclick show a toast of own description
        viewHolder.bgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(mContaxt,"Own Description", Toast.LENGTH_LONG);
                toast.setText(ownedUser+": "+ownDesc);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setGravity(0,0,0);
                toast.show();
            }
        });
    }

    /**
     * get num of owns in log
     * @return logs size
     */
    @Override
    public int getItemCount() {
        return logs.size();
    }

    /**
     * this sub class takes care of the view of each own log
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView userPic;
        ImageView ownType;
        LinearLayout bgLayout;

        /**
         * constructor of own log
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userPic = itemView.findViewById(R.id.userPic_game);
            ownType = itemView.findViewById(R.id.ownType_game);
            bgLayout = itemView.findViewById(R.id.player_bg);
        }
    }


}
