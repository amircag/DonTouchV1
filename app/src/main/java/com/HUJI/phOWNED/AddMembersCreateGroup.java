package com.HUJI.phOWNED;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This class is responsible for adding members in the creating new group process. This class
 * display only your content that already have the app and lets the user chose from them.
 * The display is interactive and let's the user add or remove members from the new
 * team that been created
 */

public class AddMembersCreateGroup extends AppCompatActivity {
    //instance of the data base
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button backBt;
    private FloatingActionButton continueBt;
    //default massage for the initial display
    private final String choose_friends_msg = "Please select at least 1 friend to add to the group.";

    NewGroupMembersAdapter adapter;
    TextView hint_for_recycler;
    Adapter_for_Android_Contacts listadapter;
    List<Android_Contact> mList_Android_Contacts;
    ArrayList<Android_Contact> arrayList_Android_Contacts = new ArrayList<Android_Contact>();
    ArrayList<Android_Contact> MembersToAdd = new ArrayList<Android_Contact>();
    ArrayList<Android_Contact> allContacts = new ArrayList<>();

    private AddMembersCreateGroup self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members_create_group);
        //get the contacts
        fp_get_Android_Contacts();
//        initPicMembersToAdd();
        //ini the added member view
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
                    intent.putExtras(getIntent().getExtras());
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

    /**
     * this method insert or remove new member from the team, add if the member is not in the list
     * and remove if in.
     * @param position in the list of contacts
     */
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

    /**
     * this method is responsible for the added members view. it will show the pic and name of each
     * added member in horizontal view and will adopt when a change as been made
     * (adding or removing member)
     */
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

    /**
     * this method get all the contact from the user phone and insert it into
     * Android_Contact obj list
     */
    public void fp_get_Android_Contacts() {

        //----------------< fp_get_Android_Contacts() >----------------

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
            return;
        }

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
                        if (phoneNumber.length() > 9) {
                            android_contact.android_contact_TelefonNr = phoneNumber;
                        }
                        //</ set >
                    }
                    phoneCursor.close();
                }
                //----</ set >----
                //----</ get phone number >----

                // Add the contact to the ArrayList
                allContacts.add(android_contact);
                //havePhownd(android_contact);
            }
            //----</ @Loop: all Contacts >----
            setApplicationContacts(allContacts);
            //< show results >
            //</ show results >


        }
        //----</ Check: hasContacts >----

    // ----------------</ fp_get_Android_Contacts() >----------------
    }

    /**
     * ask for permission to access the user contacts.
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                Toast.makeText(this, "please grant contacts permission", Toast.LENGTH_SHORT).show();
            }
            fp_get_Android_Contacts();
        }
    }

    /**
     * this method check for all the contacts in the user phone if they have the app, if so it will
     * add them to contact display list, and init the recycler.
     * @param allContacts all the user contact
     */
    private void setApplicationContacts(final ArrayList<Android_Contact> allContacts){
        db.collection("users")
                .orderBy("phoneNumber")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> users = queryDocumentSnapshots.getDocuments();
                if (users.size() == 0){
                    Log.e("Private Error", "no users");
                }else{
                    for(int i = 0; i<users.size(); i++){
                        for(int j = 0; j<allContacts.size(); j++){
                            if(allContacts.get(j).android_contact_TelefonNr != null &&
                                    users.get(i).getString("phoneNumber").contains(allContacts.get(j).android_contact_TelefonNr.substring(1))){
                                allContacts.get(j).Uid  = users.get(i).getId();
                                allContacts.get(j).nickName = users.get(i).getString("nickName");
                                allContacts.get(j).picUrl = users.get(i).getString("profilePic");
                                arrayList_Android_Contacts.add(allContacts.get(j));
                                break;
                            }
                        }
                    }

                    listadapter = new Adapter_for_Android_Contacts(self, arrayList_Android_Contacts);
                    final ListView listView_Android_Contacts = findViewById(R.id.listview_Android_Contacts);
                    listView_Android_Contacts.setAdapter(listadapter);
                    listView_Android_Contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        //notify the adapter on data changed
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            arrayList_Android_Contacts.get(position).changeState();
                            listadapter.notifyDataSetChanged();
                            insertNewMember(position);



                        }
                    });

                    listadapter.notifyDataSetChanged();
                }
            }
        });

    }

    /**
     * this is the list view adapter for the display of the contacts.
     */
    public class Adapter_for_Android_Contacts extends BaseAdapter {
        //----------------< Adapter_for_Android_Contacts() >----------------

        // Variables:
        Context mContext;

        RelativeLayout relativeLayout;
        ImageView addedMemberIcon;

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
