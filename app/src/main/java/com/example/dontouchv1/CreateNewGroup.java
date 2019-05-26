package com.example.dontouchv1;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class CreateNewGroup extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<Android_Contact> addedContacts = new ArrayList<>();


    NewGroup_ChosenMemberAdapter mAdapter;
    Button backBotton;
    Button createButton;
    TextInputLayout groupNickname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);
        backBotton  = findViewById(R.id.backButton_for_create_group);
        groupNickname = findViewById(R.id.group_nickName_input);
        getDisplay();
        initPicMembersToAdd();
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
                String groupName = groupNickname.getEditText().getText().toString();
                if (groupName.equals("")){
                    groupNickname.setError("please choose nickname");
                }else{
                    Map<String,Object> team = new HashMap<>();
                    team.put("name", groupName);
                    db.collection("teams").add(team).
                            addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        public void onSuccess(DocumentReference documentReference) {
                            String teamId = documentReference.getId();

                            WriteBatch batch = db.batch();
                            for (int i=0; i<addedContacts.size(); i++){
                                DocumentReference userTeamRef = db.collection("teams").document(teamId).collection("users").document(addedContacts.get(i).Uid);
                                Map<String,Object> userData = new HashMap<>();
                                userData.put("nickName", addedContacts.get(i).nickName);
                                userData.put("phoneNumber", addedContacts.get(i).android_contact_TelefonNr);
                                userData.put("picUrl", addedContacts.get(i).picUrl);
                                batch.set(userTeamRef,userData);

                                DocumentReference userRef = db.collection("users").document(addedContacts.get(i).Uid).collection("teams").document();
                                batch.set(userRef, teamId);
                            }

                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent newIntent = new Intent(CreateNewGroup.this, GroupProfileScreen.class);
                                    startActivity(newIntent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Private Error", "Failed to put team users");
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Private Error", "Failed to Create Team Document");
                        }
                    });

                }
            }
        });

    }



    private void initPicMembersToAdd(){
//        for (int i=0; i<MembersToAdd.size();i++){
//            Android_Contact curContact = MembersToAdd.get(i);
//            // TODO: 5/20/2019 change to phone num to get pic from server
//            newMemberPic.add(getMemberPic(curContact.android_contact_Name));
//            newMemberName.add(curContact.android_contact_Name);
//            System.out.println(MembersToAdd.size());
//
//        }
//        newMemberName.add("issar");
//        newMemberPic.add("isar");
//        newMemberName.add("amir");
//        newMemberPic.add("amir");
//        newMemberName.add("asaf");
//        newMemberPic.add("asaf");


    }
    private void initMembersView(){
        RecyclerView recyclerView = findViewById(R.id.recycle_added_members);
        mAdapter = new NewGroup_ChosenMemberAdapter(addedContacts,this);
        recyclerView.setAdapter(mAdapter);
        GridLayoutManager gridManager = new GridLayoutManager(this,4);
        recyclerView.setLayoutManager(gridManager);


    }


    private void getDisplay(){
/*      Android_Contact asaf = new Android_Contact();
        asaf.android_contact_Name= "asaf";
        addedContacts.add(asaf);
        Android_Contact amir = new Android_Contact();
        amir.android_contact_Name= "amir";
        addedContacts.add(amir);
        Android_Contact isar = new Android_Contact();
        isar.android_contact_Name= "isar";
        addedContacts.add(isar);
        Android_Contact noa = new Android_Contact();
        noa.android_contact_Name= "noa";
        addedContacts.add(noa);
        addedContacts.add(noa);
        addedContacts.add(noa);
        addedContacts.add(noa);
        addedContacts.add(noa);
        addedContacts.add(noa);
        addedContacts.add(noa);
        addedContacts.add(noa);
        addedContacts.add(noa);
        addedContacts.add(noa);*/

        if (getIntent().hasExtra("CHOSEN_MEMBERS")){
            /*ArrayList<String> members = getIntent().getStringArrayListExtra("CHOSEN_MEMBERS");*/
            ArrayList<Android_Contact> contactlist = (ArrayList<Android_Contact>) getIntent().getSerializableExtra("CHOSEN_MEMBERS");
            /*ArrayList<String> members = getIntent().getStringArrayListExtra("CHOSEN_MEMBERS");*/
            for (Android_Contact name:contactlist){
                addedContacts.add(name);

            }
        }




    }


}
