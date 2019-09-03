package com.HUJI.phOWNED;

import java.io.Serializable;

/**
 * this class is responsible for holding a contact in a format that we can use.
 */
public class Android_Contact implements Serializable {
    //----------------< fritzbox_Contacts() >----------------
    public String android_contact_Name;
    public String android_contact_TelefonNr;
    public String Uid;
    public String picUrl;
    public String nickName;

    public boolean added = false;

    /**
     * this method change the status of the contact.
     */
    public void changeState(){
        if (added){
            added= false;

        }else{
            added = true;
        }
    }

}
