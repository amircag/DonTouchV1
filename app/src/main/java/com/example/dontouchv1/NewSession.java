package com.example.dontouchv1;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class NewSession extends AppCompatActivity {

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
