package com.example.dontouchv1;

import java.io.Serializable;

public class Android_Contact implements Serializable {
    //----------------< fritzbox_Contacts() >----------------
    public String android_contact_Name;
    public String android_contact_TelefonNr;
    public String Uid;
    public String picUrl;
    public String nickName;

    public boolean added = false;

    public void changeState(){
        if (added){
            added= false;

        }else{
            added = true;
        }
    }

}
