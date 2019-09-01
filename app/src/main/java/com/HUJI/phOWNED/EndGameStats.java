package com.HUJI.phOWNED;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

import java.util.List;

public class EndGameStats extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private long timeOnPhone,duration;
    private int myScore;
    private String teamId,gameId,teamPicUrl ,gameName,myPicUrl,myNickName,teamName,durationAsText;
    private int teamOwned, myOwnedCount,myRank;
    private ArrayList<LeaderBoardObj> leaderBoardObjs = new ArrayList<>();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game_stats);


        /*// Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);*/

        Intent intent = getIntent();
        teamId = intent.getStringExtra("TEAM_ID");
        teamPicUrl = intent.getStringExtra("TEAM_PIC_URL");
        gameId = intent.getStringExtra("GAME_ID");
        teamOwned = intent.getIntExtra("OWNS_COUNT",0);
        myOwnedCount = intent.getIntExtra("MY_OWNS_COUNT",0);
        /*timeOnPhone= intent.getLongExtra("MY_WASTE_TIME",0);*/
        gameName= intent.getStringExtra("GAME_NAME");
        setLeaderBoard();


    }

    public void setLeaderBoard(){
        //
        System.out.println(gameId);
        System.out.println("reached the leaderboard func");
        db.collection("games").document(gameId)
                .collection("players")
                .orderBy("myOwnsCount", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
             @Override
             public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                 List<DocumentSnapshot> members = queryDocumentSnapshots.getDocuments();
                 System.out.println("hello");
                 for (DocumentSnapshot member: members){
                     String ownsCount = String.valueOf(member.get("myOwnsCount"));
                     LeaderBoardObj player = new LeaderBoardObj(member.getString("userId"),
                             member.getString("userPicUrl"),member.getString("userNickname"),
                             ownsCount);

                     leaderBoardObjs.add(player);
                     if (member.getString("userId").equals(user.getUid())){
                         timeOnPhone = (Long)member.get("myWasteTime");
                     }
                 }
                 System.out.println("reach SetDUration caller");
                 setDuration();

             }
         });
    }

    public void setDuration(){
        db.collection("games").document(gameId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Long createdAt =((Timestamp)documentSnapshot.get("createdAt")).getSeconds();
                Long endedAt = ((Timestamp)documentSnapshot.get("endedAt")).getSeconds();
                duration = endedAt-createdAt;

                String seconds =String.valueOf(duration % 60);
                String minutes = String.valueOf ((duration / (60)) % 60);
                String hours   = String.valueOf ((duration / (60*60)) % 24);
                if (minutes.length() == 1){
                    minutes = "0"+minutes;
                }
                if (hours.length()==1){
                    hours="0"+hours;
                }
                if (seconds.length() == 1){
                    seconds= "0"+seconds;
                }
                durationAsText = hours+":"+minutes+":"+seconds;
                setTeamName();
            }
        });
    }

    private void setTeamName(){
        db.collection("teams").document(teamId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                teamName = documentSnapshot.getString("name");

                saveDb();
            }
        });
    }

    public void saveDb(){
        calculateScore();
        DocumentReference teamRAF = db.collection("teams").document(teamId);
        DocumentReference userRAF = db.collection("users").document(user.getUid());
        WriteBatch batch = db.batch();
        batch.update(teamRAF,"firstPlace",leaderBoardObjs.get(0).getUserUid());
        batch.update(teamRAF,"lastPlace",leaderBoardObjs.get(leaderBoardObjs.size()-1)
                .getUserUid());
        batch.update(userRAF,"myOwnsCount", FieldValue.increment(myOwnedCount));
        batch.update(userRAF,"myGamesCount",FieldValue.increment(1));
        batch.update(userRAF,"myTotalScore",FieldValue.increment(myScore));
        batch.update(userRAF,"wastedTime",FieldValue.increment(timeOnPhone));


        batch.commit();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_end_game_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void calculateScore(){
        Long durationImMil = duration*1000;
        double score =100*(1-((double)timeOnPhone/((double)durationImMil)));
        System.out.println(timeOnPhone);
        System.out.println(durationImMil);
        System.out.println(score);
        myScore = (int)Math.round(score);

        if (myScore >= 98 && myOwnedCount == 0){
            myScore = 100;
        }
        if (myScore <=0){
            myScore = 1;
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_end_game_stats, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment = null;
            switch (position){
                case 0:
                    fragment = new EndGameGroupStats();
                    fragment.setArguments(toGroupStats());
                    break;
                case 1:
                    fragment = new EndGamePersonalStat();
                    fragment.setArguments(toPersonalStats());
                    break;
            }return fragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }

    @Override
    public void onBackPressed() {

    }

    public Bundle toGroupStats(){
        Bundle myB = new Bundle();
        myB.putInt("OWNES_COUNT",teamOwned);
        myB.putString("DURATION",durationAsText);
        myB.putString("GAME_NAME",gameName);
        myB.putString("TEAM_PIC_URL",teamPicUrl);
        myB.putSerializable("LEADER_BOARD",leaderBoardObjs);
        myB.putString("TEAM_NAME",teamName);


        return myB;
    }

    public Bundle toPersonalStats(){
        setMyData();

        Bundle bundle = new Bundle();
        bundle.putInt("MY_SCORE",myScore);
        bundle.putInt("MY_RANK",myRank);
        bundle.putInt("MY_OWMES_COUNT",myOwnedCount);
        bundle.putLong("TIME_ON_PHONE",timeOnPhone);
        bundle.putString("MY_PIC_URL",myPicUrl);
        bundle.putString("MY_NICK_NAME",myNickName);
        bundle.putString("GAME_ID",gameId);
        return bundle;
    }

    private void setMyData(){
        for (int i=0; i< leaderBoardObjs.size();i++){
            if(user.getUid().equals(leaderBoardObjs.get(i).getUserUid())){
                myRank = i+1;
                myNickName = leaderBoardObjs.get(i).getNickName();
                myPicUrl = leaderBoardObjs.get(i).getPicUrl();
                break;
            }
        }
    }




}
