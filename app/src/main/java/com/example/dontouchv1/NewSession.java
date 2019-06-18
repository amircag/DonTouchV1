package com.example.dontouchv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class NewSession extends AppCompatActivity {
    private int selectedGame = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //((RadioGroup) findViewById(R.id.toggleGroup)).setOnCheckedChangeListener(ToggleListener);
    }

    static final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                LinearLayout lyout = (LinearLayout) radioGroup.getChildAt(j);
                for (int k = 0; k<lyout.getChildCount(); k++){
                    try{
                        final ToggleButton view = (ToggleButton) lyout.getChildAt(k);
                        view.setChecked(view.getId() == i);
                    } catch (Exception e){

                    }
                }
            }
        }
    };

    public void onToggle(View view) {
        ((RadioGroup)view.getParent().getParent()).check(view.getId());
        // app specific stuff ..
    }

    public void onTypeSelected(View view){
        LinearLayout linearLayout = (LinearLayout)view.getParent();
        for (int i=0; i<linearLayout.getChildCount(); i++){
            try{
                final LinearLayout button = (LinearLayout) linearLayout.getChildAt(i);
                button.setBackgroundResource(R.drawable.profile_border);
            } catch(Exception e){

            }
        }
        view.setBackgroundResource(R.drawable.profile_border_selected);

        // set the right description to arrive
        linearLayout = (LinearLayout) findViewById(R.id.games_desc);
        for (int i=0; i<linearLayout.getChildCount(); i++){
            try{
                final TextView desc = (TextView) linearLayout.getChildAt(i);
                desc.setVisibility(View.GONE);
            } catch (Exception e){

            }
        }
        switch (view.getId()){
            case R.id.chill:
                findViewById(R.id.chill_desc).setVisibility(View.VISIBLE);
                break;
            case R.id.thrill:
                findViewById(R.id.thrill_desc).setVisibility(View.VISIBLE);
                break;
            case R.id.kill:
                findViewById(R.id.kill_desc).setVisibility(View.VISIBLE);
                break;
            default:
        }

        //updates the selected game id var
         selectedGame = view.getId();
    }

    public void onClickCreate(View view){
        if(selectedGame != -1){
            EditText gameName = (EditText) findViewById(R.id.gameName);
            String text = gameName.getText().toString();

            Intent intent = new Intent(this, GameScreen.class);
            intent.putExtra("GAME_TYPE", selectedGame);
            intent.putExtra("GAME_NAME", text);
            startActivity(intent);
        }
    }
/*
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(NewSession.this, Statistics.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/
}
