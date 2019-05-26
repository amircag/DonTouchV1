package com.example.dontouchv1;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddMembersCreateGroup extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button backBt;
    private FloatingActionButton continueBt;

    private final String choose_friends_msg = "Please select at least 1 friend to add to the group.";

    NewGroupMembersAdapter adapter;
    TextView hint_for_recycler;
    Adapter_for_Android_Contacts listadapter;
    List<Android_Contact> mList_Android_Contacts;
    ArrayList<Android_Contact> arrayList_Android_Contacts = new ArrayList<Android_Contact>();
    ArrayList<Android_Contact> MembersToAdd = new ArrayList<Android_Contact>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members_create_group);
        fp_get_Android_Contacts();
        initPicMembersToAdd();
        initMembersView();
        hint_for_recycler = findViewById(R.id.hint_for_add_members_text);
        backBt = findViewById(R.id.backButton_for_add_members);
        backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        continueBt = findViewById(R.id.continue_bt_add_members);
        continueBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MembersToAdd.size()>0) {
                    Intent intent = new Intent(AddMembersCreateGroup.this, CreateNewGroup.class);
                /*ArrayList<String> membersNames = passingContactsId();

                intent.putExtra("CHOSEN_MEMBERS",membersNames);*/
                    intent.putExtra("CHOSEN_MEMBERS", MembersToAdd);
                    startActivity(intent);
                }else {
                    Toast message = Toast.makeText(AddMembersCreateGroup.this,choose_friends_msg,Toast.LENGTH_LONG);
                    message.setGravity(0,0,0);
                    message.show();
                }

            }
        });

    }

    private ArrayList<String> passingContactsId(){
        ArrayList<String> membersNames = new ArrayList<>();
        for (Android_Contact contact:MembersToAdd){
            membersNames.add(contact.android_contact_Name);

        }return membersNames;
    }

    public void insertNewMember(int position){
        Android_Contact curContact = arrayList_Android_Contacts.get(position);
        if (curContact.added) {
            MembersToAdd.add(arrayList_Android_Contacts.get(position));
            adapter.notifyItemInserted(MembersToAdd.size()-1);
        }else {
            MembersToAdd.remove(curContact);
            adapter.notifyDataSetChanged();
        }


        if (hint_for_recycler.getVisibility() == View.VISIBLE) {
            hint_for_recycler.setVisibility(View.INVISIBLE);
        }
        if (MembersToAdd.size() == 0){
            hint_for_recycler.setVisibility(View.VISIBLE);
        }


    }

    private void initPicMembersToAdd(){
//        for (int i=0; i<MembersToAdd.size();i++){
//            Android_Contact curContact = MembersToAdd.get(i);
//            // TODO: 5/20/2019 change to phone num to get pic from server
//            newMemberPic.add(getMemberPic(curContact.android_contact_Name));
//            newMemberName.add(curContact.android_contact_Name);
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
        RecyclerView recyclerView = findViewById(R.id.newGroupMembersRecyclerView);
        adapter = new NewGroupMembersAdapter(MembersToAdd,this);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linaiarManagernewGroup = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linaiarManagernewGroup);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                for (Android_Contact contact: mList_Android_Contacts){
                    System.out.println("list:"+adapter.contactsToAdd.size());
                    if (!adapter.contactsToAdd.contains(contact) && contact.added){

                        mList_Android_Contacts.get(mList_Android_Contacts.indexOf(contact)).changeState();
                        listadapter.notifyDataSetChanged();

                        if (MembersToAdd.size() == 0){
                            hint_for_recycler.setVisibility(View.VISIBLE);
                        }


                    }
                }
            }
        });

    }

    private String getMemberPic(String phoneNum){
        // TODO: 5/19/2019 get pic from server
        return phoneNum;
    }


    private void havePhownd(final Android_Contact android_contact){
        String pn = "+" + android_contact.android_contact_TelefonNr;
        db.collection("users")
                .orderBy("phoneNumber")
                .whereGreaterThanOrEqualTo("phoneNumber", pn)
                .whereLessThanOrEqualTo("phoneNumber", pn)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> users = queryDocumentSnapshots.getDocuments();
                if (users.size() > 1) {
                    Log.e("Private Error", "more than one user for one phone number " + android_contact.android_contact_TelefonNr);
                    System.out.println(users);
                } else if (users.size() == 0){
                    Log.e("Private Error", "no user with phone number " + android_contact.android_contact_TelefonNr);
                } else{
                    DocumentSnapshot user = users.get(0);
                    android_contact.Uid = user.getId();
                    android_contact.nickName = user.getString("nickName");
                    android_contact.picUrl = user.getString("profilePic");
                    arrayList_Android_Contacts.add(android_contact);
                    listadapter.notifyDataSetChanged();
                }
            }
        });
    }


    public void fp_get_Android_Contacts() {
//----------------< fp_get_Android_Contacts() >----------------


//--< get all Contacts >--
        Cursor cursor_Android_Contacts = null;
        ContentResolver contentResolver = getContentResolver();
        try {
            cursor_Android_Contacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        } catch (Exception ex) {
            Log.e("Error on contact", ex.getMessage());
        }
//--</ get all Contacts >--

//----< Check: hasContacts >----
        if (cursor_Android_Contacts.getCount() > 0) {
//----< has Contacts >----
//----< @Loop: all Contacts >----
            while (cursor_Android_Contacts.moveToNext()) {
//< init >
                Android_Contact android_contact = new Android_Contact();
                String contact_id = cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts._ID));
                String contact_display_name = cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//</ init >

//----< set >----
                android_contact.android_contact_Name = contact_display_name;


//----< get phone number >----
                int hasPhoneNumber = Integer.parseInt(cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                            , null
                            , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                            , new String[]{contact_id}
                            , null);

                    while (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//< set >
                        phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
                        android_contact.android_contact_TelefonNr = phoneNumber;

//</ set >
                    }
                    phoneCursor.close();
                }
//----</ set >----
//----</ get phone number >----

// Add the contact to the ArrayList
                havePhownd(android_contact);
            }
//----</ @Loop: all Contacts >----

//< show results >
            listadapter = new Adapter_for_Android_Contacts(this, arrayList_Android_Contacts);
            final ListView listView_Android_Contacts = findViewById(R.id.listview_Android_Contacts);
            listView_Android_Contacts.setAdapter(listadapter);
            listView_Android_Contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    arrayList_Android_Contacts.get(position).changeState();
                    listadapter.notifyDataSetChanged();
                    insertNewMember(position);



                }
            });
//</ show results >


        }
//----</ Check: hasContacts >----

// ----------------</ fp_get_Android_Contacts() >----------------
    }

    public class Adapter_for_Android_Contacts extends BaseAdapter {
        //----------------< Adapter_for_Android_Contacts() >----------------
//< Variables >
        Context mContext;

        RelativeLayout relativeLayout;
        ImageView addedMemberIcon;
//</ Variables >

        //< constructor with ListArray >
        public Adapter_for_Android_Contacts(Context mContext, List<Android_Contact> mContact) {
            this.mContext = mContext;
            mList_Android_Contacts = mContact;
            relativeLayout = findViewById(R.id.contactsView);
//            addedMemberIcon = findViewById(R.id.added_member_icon);
        }
//</ constructor with ListArray >

        @Override
        public int getCount() {
            return mList_Android_Contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return mList_Android_Contacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //----< show items >----
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = View.inflate(mContext,R.layout.contacts_text_view,null);
//< get controls >
            TextView textview_contact_Name= (TextView) view.findViewById(R.id.textview_android_contact_name);
            CircleImageView memberPic = view.findViewById(R.id.contactPic);
            addedMemberIcon = view.findViewById(R.id.added_member_icon);
//< show values >
            textview_contact_Name.setText(mList_Android_Contacts.get(position).android_contact_Name);
            /*
            int imageId = mContext.getResources().getIdentifier("drawable/"+ mList_Android_Contacts.get(position).android_contact_Name,null,mContext.getPackageName());
            memberPic.setImageResource(imageId);
            */
            Glide.with(AddMembersCreateGroup.this)
                    .load(arrayList_Android_Contacts.get(position).picUrl)
                    .disallowHardwareConfig()
                    .into(memberPic);
            if (mList_Android_Contacts.get(position).added) {
                addedMemberIcon.setVisibility(view.VISIBLE);
            }
            else {
                addedMemberIcon.setVisibility(View.INVISIBLE);
            }

//</ show values >


            view.setTag(mList_Android_Contacts.get(position).android_contact_Name);
            return view;
        }


//----</ show items >----
//----------------</ Adapter_for_Android_Contacts() >----------------
    }
}
