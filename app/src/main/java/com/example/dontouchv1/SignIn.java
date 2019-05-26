package com.example.dontouchv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SignIn extends AppCompatActivity {

    private int RC_SIGN_IN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sign_in);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            /*Intent intent = new Intent(this,HomeScreen.class);*/
            Intent intent = new Intent(this,SplashScreen.class);
            intent.putExtra("USER_ID", user.getUid());
            intent.putExtra("USER_PHOTO", user.getPhotoUrl());
            intent.putExtra("NEW_USER", false);
            startActivity(intent);
            finish();

        } else {
            // No user is signed in

            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.PhoneBuilder().build());

            Random rand = new Random();
            RC_SIGN_IN = Math.abs(rand.nextInt(100000));

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setLogo(R.drawable.phowned_with_text)
                            .setAlwaysShowSignInMethodScreen(true)
                            .setTheme(R.style.SignInStyle)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                Intent intent = new Intent(this,NewProfile.class);
                intent.putExtra("USER_ID", user.getUid());
                intent.putExtra("USER_PHOTO", user.getPhotoUrl());
                intent.putExtra("NEW_USER", true);
                startActivity(intent);
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                if (response == null){
                    //back button id pressed
                    finish();
                }
            }
        }
    }

}

