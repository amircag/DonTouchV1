package com.HUJI.phOWNED;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * this class is responsible for creating a new group. that mean it will let the user upload a
 * group pic and name. the class will display the added members(pic & name).
 */
public class CreateNewGroup extends AppCompatActivity {
    //instance of the data base
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //instance of the user
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Uri filePath;
    private String picUrl;
    private String groupName;
    private ArrayList<Android_Contact> addedContacts = new ArrayList<>();
    private static int PICK_IMAGE_REQ = 23;

    NewGroup_ChosenMemberAdapter mAdapter;
    Button backBotton;
    FloatingActionButton createButton;
    TextInputLayout groupNickname;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_new_group);
        backBotton  = findViewById(R.id.backButton_for_create_group);
        groupNickname = findViewById(R.id.group_nickName_input);
        //display the added members
        getDisplay();
        initMembersView();
        backBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        createButton = findViewById(R.id.create_group_bt);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupName = groupNickname.getEditText().getText().toString();
                if (groupName.equals("")){
                    groupNickname.setError("please choose nickname");
                }else{
                    save();//and after that UploadImage() and then savedb()
                }
            }
        });

    }

    /**
     * this method create the group in the data base
     */
    private void save(){
        Map<String,Object> team = new HashMap<>();
        team.put("name", groupName);
        db.collection("teams").add(team).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    public void onSuccess(DocumentReference documentReference) {
                        String teamId = documentReference.getId();
                        uploadImage(teamId);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Private Error", "Failed to Create Team Document");
            }
        });
    }

    /**
     * this method upload a chosen from the user pic for a given group
     * @param teamId
     */
    private void uploadImage(final String teamId){
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            StorageReference ref = storageReference.child("teams/" + teamId + "/" + UUID.randomUUID());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(CreateNewGroup.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());

                            picUrl = uri.getResult().toString();
                            /*saveDB(teamId);*/
                            addSelfAndSaveDB(new Android_Contact(),teamId);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CreateNewGroup.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    /**
     * this method add all the info of the group to the data base and save it under the group ID
     * @param teamId the group to save
     */
    private void saveDB(final String teamId){
        WriteBatch batch = db.batch();
        DocumentReference teamRef = db.collection("teams").document(teamId);
        Map<String,Object> pic = new HashMap<>();
        pic.put("picUrl", picUrl);
        pic.put("name",groupName);
        pic.put("createdAt", FieldValue.serverTimestamp());
        pic.put("firstPlace",null);
        if (addedContacts.size() > 1) {
            pic.put("lastPlace", null);
        } else {
            pic.put("lastPlace", null);
        }
        batch.set(teamRef, pic);
        //add all the necessary members info to the group
        for (int i=0; i<addedContacts.size(); i++){
            DocumentReference userTeamRef = db.collection("teams").document(teamId).collection("users").document(addedContacts.get(i).Uid);
            Map<String,Object> userData = new HashMap<>();
            userData.put("nickName", addedContacts.get(i).nickName);
            userData.put("phoneNumber", addedContacts.get(i).android_contact_TelefonNr);
            userData.put("picUrl", addedContacts.get(i).picUrl);
            batch.set(userTeamRef,userData);

            DocumentReference userRef = db.collection("users").document(addedContacts.get(i).Uid).collection("teams").document(teamId);
            Map<String,Object> data = new HashMap<>();
            data.put("picUrl",picUrl);
            data.put("name",groupName);
            batch.set(userRef,data);
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> task) {
                Intent newIntent = new Intent(CreateNewGroup.this, GroupProfileScreen.class);
                newIntent.putExtra("TEAM_ID", teamId);
                newIntent.putExtras(getIntent().getExtras());
                startActivity(newIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Private Error", "Failed to put team users");
            }
        });
    }

    /**
     * this method init the added members display
     */
    private void initMembersView(){
        RecyclerView recyclerView = findViewById(R.id.recycle_added_members);
        mAdapter = new NewGroup_ChosenMemberAdapter(addedContacts,this);
        recyclerView.setAdapter(mAdapter);
        GridLayoutManager gridManager = new GridLayoutManager(this,4);
        recyclerView.setLayoutManager(gridManager);


    }

    /**
     * this method get the added members from the intent
     */
    private void getDisplay(){


        if (getIntent().hasExtra("CHOSEN_MEMBERS")){
            ArrayList<Android_Contact> contactlist = (ArrayList<Android_Contact>) getIntent().getSerializableExtra("CHOSEN_MEMBERS");
            /*ArrayList<String> members = getIntent().getStringArrayListExtra("CHOSEN_MEMBERS");*/
            for (Android_Contact name:contactlist){
                addedContacts.add(name);
            }
        }
    }

    /**
     * this method add the user to the group and save all the group in the data base using save
     * method
     * @param contact the user contact info as Android_Contact obj
     * @param teamId the ID of the team to save in.
     */
    public void addSelfAndSaveDB(final Android_Contact contact, final String teamId){
        db.collection("users")
                .whereEqualTo("phoneNumber",user.getPhoneNumber())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> result = queryDocumentSnapshots.getDocuments();
                if (result.size() > 1){
                    Log.e("Private Error", "more than one user for one phone number " + user.getPhoneNumber());
                    System.out.println(result);
                } else {
                    DocumentSnapshot selfUser = result.get(0);
                    contact.android_contact_TelefonNr = selfUser.getString("phoneNumber");
                    contact.picUrl = selfUser.getString("profilePic");
                    contact.nickName = selfUser.getString("nickName");
                    contact.Uid = user.getUid();
                    addedContacts.add(contact);
                    saveDB(teamId);

                }

            }
        });


    }

    public void getTeamPic(View view){
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
                ImageView imageView = (ImageView) findViewById(R.id.GroupImageForNewGroup);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

}
