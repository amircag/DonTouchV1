package com.HUJI.phOWNED;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class EditGroup extends AppCompatActivity {

    final int semiTransparentGrey = Color.argb(155, 41, 36, 33);

    private static final int PICK_IMAGE_REQ = 0;
    private Uri filePath;


    private EditGroup self = this;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String teamId,teamPicUrl,teamName;
    private String newPicUrl, newTeamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        teamId = getIntent().getStringExtra("TEAM_ID");
        teamPicUrl = getIntent().getStringExtra("TEAM_PIC");
        teamName = getIntent().getStringExtra("TEAM_NAME");
        displayCurrentData();
        setExitGroupButton();
    }


    /**
     * Load team's current Image and Name for the screen
     */
    private void displayCurrentData(){
        EditText enterGroupname = findViewById(R.id.edit_group_edit_name);
        ImageView choosePic = findViewById(R.id.edit_group_imageview);
        choosePic.setColorFilter(semiTransparentGrey, PorterDuff.Mode.SRC_ATOP);

        enterGroupname.setText(teamName);
        Glide.with(this)
                .load(teamPicUrl)
                .into(choosePic);
    }

    public void backToPrevScreen(View view){
        onBackPressed();
        finish();
    }

    public void chooseNewTeamImage(View view){
        Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,PICK_IMAGE_REQ);
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
                ImageView imageView = (ImageView) findViewById(R.id.edit_group_imageview);
                imageView.setImageBitmap(bitmap);
                imageView.clearColorFilter();
                ImageView camera = findViewById(R.id.editGroupCamera);
                camera.setVisibility(View.INVISIBLE);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void saveGroupChangesPressed(View view){
        saveProfileChanges(filePath);
    }

    private void saveProfileChanges(Uri filePath) {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final UUID filename = UUID.randomUUID();
            StorageReference ref = storageReference.child("teams/" + teamId + "/" + UUID.randomUUID());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditGroup.this, "Image Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());

                            newPicUrl = uri.getResult().toString();

                            HashMap<String,Object> data = new HashMap<>();
                            data.put("picUrl", newPicUrl);
                            newTeamName = ((EditText)findViewById(R.id.edit_group_edit_name)).getText().toString();
                            data.put("name",newTeamName);
                            db.collection("teams").document(teamId)
                                    .update(data);

                            updateGroupDataForUsers(newTeamName,newPicUrl);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditGroup.this, "Image Upload Failed."+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            HashMap<String,Object> data = new HashMap<>();
            newTeamName = ((EditText)findViewById(R.id.edit_group_edit_name)).getText().toString();
            data.put("name",newTeamName);
            db.collection("teams").document(teamId)
                    .update(data);
            updateGroupDataForUsers(newTeamName, null);

        }
    }


    private void updateGroupDataForUsers(final String groupName, final String picUrl){

        db.collection("teams")
                .document(teamId)
                .collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> teamUsers = queryDocumentSnapshots.getDocuments();
                        ArrayList<String> userIds = new ArrayList<>();
                        for (DocumentSnapshot user: teamUsers){
                            userIds.add(user.getId());
                        }

                        HashMap<String,Object> data = new HashMap<>();
                        data.put("name", groupName);

                        if (picUrl != null){
                            data.put("picUrl", picUrl);
                        }

                        for (String id : userIds){
                            db.collection("users")
                                    .document(id)
                                    .collection("teams")
                                    .document(teamId)
                                    .update(data);
                        }

                        Intent intent = new Intent(EditGroup.this, GroupProfileScreen.class);
                        intent.putExtra("TEAM_ID",teamId);
                        startActivity(intent);
                        finish();
                    }
                });


    }


    private void setExitGroupButton(){

        Button exitGroupButton = findViewById(R.id.edit_group_exit_button);

        exitGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(self)
                        .setTitle("Are you SURE want to leave?")
                        .setMessage("You will not be able to join this team once leaving it!")
                        .setIcon(R.drawable.exit_group)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                removeUser();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

    }

    private void removeUser(){

        // delete team from user's collection
        db
                .collection("users")
                .document(user.getUid())
                .collection("teams")
                .document(teamId)
                .delete();

        // delete user from team's collection
        db
                .collection("teams")
                .document(teamId)
                .collection("users")
                .document(user.getUid())
                .delete();

        Intent homeScreen = new Intent(EditGroup.this,HomeScreen.class);
        startActivity(homeScreen);
        finish();



    }

}
