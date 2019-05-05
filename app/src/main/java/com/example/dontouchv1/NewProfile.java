package com.example.dontouchv1;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class NewProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent fromIntent = getIntent();
        if (!fromIntent.getBooleanExtra("NEW_USER",false)){
            Intent intentTo = new Intent(this, HomeScreen.class);
            startActivity(intentTo);
            return;
        }

        setContentView(R.layout.activity_new_profile);

        initUserInfo();
    }

    public void pickFromGallery(View view){
        Intent intent= new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,0);
    }

    private void initUserInfo(){
        ImageView profile = (ImageView) findViewById(R.id.newProfilePic);
        profile.setImageResource(R.drawable.ic_person_black_24dp);
/*
        Cursor c = getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        c.moveToFirst();
        TextInputEditText text = (TextInputEditText) findViewById(R.id.nameInput);
        text.setText(c.getString(c.getColumnIndex("display_name")));
        c.close();
*/
    }

    public void onClickSave(View view){
        Intent intent = new Intent(this,HomeScreen.class);
        //intent.putExtra("PROFILE_EMAIL",((EditText)findViewById(R.id.profileEmail)).getText());
        //intent.putExtra("PROFILE_IMAGE_BYTE", bs.toByteArray());
        startActivity(intent);
    }
}
