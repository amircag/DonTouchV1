package com.example.dontouchv1;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {

    private final int WAIT_DURATION = 3500;
    private Handler timer;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<String> userData;

    String nicknameString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash_screen);

        userData = getUsername();

        timer = new Handler();
        timer.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
                intent.putStringArrayListExtra("USER DATA",userData);
                startActivity(intent);
                finish();
            }
        }, WAIT_DURATION);

    }


    private ArrayList<String> getUsername(){
        final ArrayList<String> dataFromServer = new ArrayList<>();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        //DocumentSnapshot doc = docRef.get(Source.DEFAULT).getResult();
        docRef.get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    setContentView(R.layout.activity_splash_screen);
                    if (document.getString("nickName") != null) {
                        nicknameString = document.getString("nickName");
                        dataFromServer.add(nicknameString);
                        if(document.getString("profilePic") != null){
                            dataFromServer.add(document.getString("profilePic"));
                        }
                        TextView message = findViewById(R.id.welcome_message);
                        String displayMessage = getString(R.string.splash_dynamic_message,nicknameString);
                        message.setText(displayMessage);
                    } else {
                        setContentView(R.layout.activity_splash_screen);
                    }
                } else {
                    setContentView(R.layout.activity_splash_screen);
                }
            }
        });
        return dataFromServer;
    }




        /*String nicknameString = doc.getString("nickName");
        if (nicknameString != null){
            return nicknameString;
        }
        else{
            return "friend";
        }*/

}
