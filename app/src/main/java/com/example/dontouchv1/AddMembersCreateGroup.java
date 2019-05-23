package com.example.dontouchv1;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddMembersCreateGroup extends AppCompatActivity {

    private ArrayList<String> newMemberPic = new ArrayList<>();
    private ArrayList<String> newMemberName = new ArrayList<>();
    private ArrayList<String> newMemberId = new ArrayList<>();
    NewGroupMembersAdapter adapter;
    TextView hint_for_recycler;

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
    }

    public void insertNewMember(int position){
        MembersToAdd.add(arrayList_Android_Contacts.get(position));
        Android_Contact curContact = MembersToAdd.get(MembersToAdd.size()-1);
        newMemberPic.add(getMemberPic(curContact.android_contact_Name));
        newMemberName.add(curContact.android_contact_Name);
        newMemberId.add(curContact.android_contact_TelefonNr);
        adapter.notifyItemInserted(MembersToAdd.size()-1);

        if (hint_for_recycler.getVisibility() == View.VISIBLE) {
            hint_for_recycler.setVisibility(View.INVISIBLE);
        }


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
        RecyclerView recyclerView = findViewById(R.id.newGroupMembersRecyclerView);
        adapter = new NewGroupMembersAdapter(MembersToAdd,this);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linaiarManagernewGroup = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linaiarManagernewGroup);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });

    }

    private String getMemberPic(String phoneNum){
        // TODO: 5/19/2019 get pic from server
        return phoneNum;
    }


    private boolean havePhownd(String phoneNum){
        // TODO: 5/19/2019 get from server if this phone register
        if(phoneNum.equals("45566")){
            return false;
        }
        return true;
    }

//    public class Android_Contact {
//        //----------------< fritzbox_Contacts() >----------------
//        public String android_contact_Name = "";
//        public String android_contact_TelefonNr = "";
//        public String android_contact_ID="";
//        public boolean added = false;
//
//        public void changeState(){
//            if (added){
//                added= false;
//
//            }else{
//                added = true;
//            }
//        }
////----------------</ fritzbox_Contacts() >----------------
//    }

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
                if (havePhownd(android_contact.android_contact_TelefonNr)) {
                    arrayList_Android_Contacts.add(android_contact);
                }

            }
//----</ @Loop: all Contacts >----

//< show results >
            final Adapter_for_Android_Contacts adapter = new Adapter_for_Android_Contacts(this, arrayList_Android_Contacts);
            final ListView listView_Android_Contacts = findViewById(R.id.listview_Android_Contacts);
            listView_Android_Contacts.setAdapter(adapter);
            listView_Android_Contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    arrayList_Android_Contacts.get(position).changeState();
                    adapter.notifyDataSetChanged();
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
        List<Android_Contact> mList_Android_Contacts;
        RelativeLayout relativeLayout;
        ImageView addedMemberIcon;
//</ Variables >

        //< constructor with ListArray >
        public Adapter_for_Android_Contacts(Context mContext, List<Android_Contact> mContact) {
            this.mContext = mContext;
            this.mList_Android_Contacts = mContact;
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
            int imageId = mContext.getResources().getIdentifier("drawable/"+ mList_Android_Contacts.get(position).android_contact_Name,null,mContext.getPackageName());
            memberPic.setImageResource(imageId);
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
