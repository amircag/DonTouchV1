package com.example.dontouchv1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class NewProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQ = 0;
    private Uri filePath;
    private String picUrl;
    private String picFilename;

    final int semiTransparentGrey = Color.argb(155, 41, 36, 33);

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private NewProfile self;
    
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
        self = this;
        initUserInfo();
    }

    public void pickFromGallery(View view){
        Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,PICK_IMAGE_REQ);
    }

    private void initUserInfo(){
        ImageView profile = (ImageView) findViewById(R.id.newProfilePic);
        /*profile.setImageResource(R.drawable.ic_person_black_24dp);*/
        profile.setColorFilter(semiTransparentGrey, PorterDuff.Mode.SRC_ATOP);
/*
        Cursor c = getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        c.moveToFirst();
        TextInputEditText text = (TextInputEditText) findViewById(R.id.nameInput);
        text.setText(c.getString(c.getColumnIndex("display_name")));
        c.close();
*/
    }

    public void onClickSave(View view){
        //intent.putExtra("PROFILE_EMAIL",((EditText)findViewById(R.id.profileEmail)).getText());
        //intent.putExtra("PROFILE_IMAGE_BYTE", bs.toByteArray());
        uploadImage(filePath);

    }

    private void saveDB(){
        String userId = user.getUid().toString();
        HashMap<String,Object> data = new HashMap<>();
        data.put("nickName",((EditText)findViewById(R.id.nickName)).getText().toString());
        data.put("phoneNumber",user.getPhoneNumber());
        data.put("profilePic", picUrl);
        data.put("profilePicFilename",picFilename);
        db.collection("users").document(userId)
                .set(data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQ && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
             filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ImageView imageView = (ImageView) findViewById(R.id.newProfilePic);
                imageView.setImageBitmap(bitmap);
                imageView.clearColorFilter();
                ImageView camera = findViewById(R.id.newProfileCamera);
                camera.setVisibility(View.INVISIBLE);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(Uri filePath) {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final UUID filename = UUID.randomUUID();
            StorageReference ref = storageReference.child("users/" + user.getUid().toString() + "/" + filename);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(NewProfile.this, "Image Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());

                            picFilename = filename.toString();
                            picUrl = uri.getResult().toString();
                            saveDB();
                            Intent intent = new Intent(self,HomeScreen.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(NewProfile.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

}
