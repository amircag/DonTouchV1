package com.HUJI.phOWNED;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This activity is responsible for the "Edit Profile" screen,
 * lets the user update their personal info
 * (changing name/profile pic)
 */
public class EditProfile extends AppCompatActivity {

    /**
     * firebase variables
     */
    private static final int PICK_IMAGE_REQ = 0;
    private Uri filePath;
    private String picUrl;
    private String picFilename;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    /**
     * Some constants
     */
    final int semiTransparentGrey = Color.argb(155, 41, 36, 33);
    private final String USER_NICKNAME = "NICKNAME";
    private final String USER_IMAGE = "PROFILE_IMAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        loadCurrentData();

        CircleImageView profileImage = findViewById(R.id.edit_profile_pic);
        profileImage.setColorFilter(semiTransparentGrey, PorterDuff.Mode.SRC_ATOP);

    }


    /**
     * Load user's current Image and Name for the screen
     */
    private void loadCurrentData(){
        EditText enterNickname = findViewById(R.id.edit_nickname);
        CircleImageView choosePic = findViewById(R.id.edit_profile_pic);


        Intent prevScreen = getIntent();
        String currNickname = prevScreen.getStringExtra(USER_NICKNAME);
        String currProfPic = prevScreen.getStringExtra(USER_IMAGE);

        enterNickname.setText(currNickname);
        Glide.with(this)
                .load(currProfPic)
                .into(choosePic);

    }

    /**
     * Sets the back arrow button
     * @param view current view
     */
    public void backToProfile(View view){
        onBackPressed();

    }


    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this,ProfileScreen.class);
        intent.putExtra("USER_ID",user.getUid());
        startActivity(intent);
        finish();
    }

    /**
     * this method lets the user choose a new image from their phone to upload as a profile picture
     * @param view current view
     */
    public void chooseImage(View view){
        Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,PICK_IMAGE_REQ);
    }

    /**
     * updates the photo on the activity to the one the user chose
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQ && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ImageView imageView = (ImageView) findViewById(R.id.edit_profile_pic);
                imageView.setImageBitmap(bitmap);

                // remove gray filter set the camera icon to invisible
                imageView.clearColorFilter();
                ImageView camera = findViewById(R.id.editProfileCamera);
                camera.setVisibility(View.INVISIBLE);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * call action when "save" button is pressed
     * @param view
     */
    public void saveChangesClick(View view){
        saveProfileChanges(filePath);
    }

    /**
     * Save the changes to the group made by the user, upload to firebase and calls next methods
     * @param filePath filepath of chosen image
     */
    private void saveProfileChanges(Uri filePath) {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final UUID filename = UUID.randomUUID();
            StorageReference ref = storageReference.child("users/" + user.getUid() + "/" + filename);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfile.this, "Image Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());

                            picUrl = uri.getResult().toString();
                            String userId = user.getUid().toString();
                            HashMap<String,Object> data = new HashMap<>();
                            data.put("profilePic", picUrl);
                            String newNickname = ((EditText)findViewById(R.id.edit_nickname)).getText().toString();
                            data.put("nickName",newNickname);
                            db.collection("users").document(userId)
                                    .update(data);

                            updateDataInGroups(newNickname,picUrl);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfile.this, "Image Upload Failed."+e.getMessage(), Toast.LENGTH_SHORT).show();
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
        else{
            String userId = user.getUid().toString();
            HashMap<String,Object> data = new HashMap<>();
            String newNickname = ((EditText)findViewById(R.id.edit_nickname)).getText().toString();
            data.put("nickName",newNickname);
            db.collection("users").document(userId)
                    .update(data);
            updateDataInGroups(newNickname, null);

        }
    }

    /**
     * updates the changes in the group under each group the user is part of, and then
     * starts the next activity
     * @param nickname user's new nickname (if applicable)
     * @param picUrl user's new profile pic url (if applicable)
     */
    private void updateDataInGroups(final String nickname, final String picUrl){
        final String userId = user.getUid().toString();

        // collect all teams user is part of
        db.collection("users")
                .document(userId)
                .collection("teams")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> userTeams = queryDocumentSnapshots.getDocuments();
                        ArrayList<String> teamIds = new ArrayList<>();
                        for (DocumentSnapshot team : userTeams){
                            teamIds.add(team.getId());
                        }

                        HashMap<String,Object> data = new HashMap<>();
                        data.put("nickName", nickname);

                        if (picUrl != null){
                            data.put("picUrl", picUrl);
                        }

                        // go through each group user is part of and updates this user's
                        // document in them
                        for (String id : teamIds){
                            db.collection("teams")
                                    .document(id)
                                    .collection("users")
                                    .document(userId)
                                    .update(data);
                        }

                        // when finished, go to user's profile
                        Intent intent = new Intent(EditProfile.this, ProfileScreen.class);
                        intent.putExtra("USER_ID",user.getUid());
                        startActivity(intent);
                        finish();
                    }
                });


    }




}
