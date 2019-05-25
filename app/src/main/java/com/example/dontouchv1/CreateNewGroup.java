package com.example.dontouchv1;


import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;


public class CreateNewGroup extends AppCompatActivity {

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
        final String groupName = groupNickname.getEditText().getText().toString().trim();
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

                if (groupName.isEmpty()){
                    groupNickname.setError("please choose nickname");
                }else{
                    onBackPressed();
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
