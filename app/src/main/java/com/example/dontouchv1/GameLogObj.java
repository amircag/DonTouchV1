package com.example.dontouchv1;

public class GameLogObj {
    private String userPicUrl;
    private String userName;
    private String ownDesc;
    private int ownType;

    public GameLogObj(String userPicUrl, String userName, String ownDesc, int ownType) {
        this.userPicUrl = userPicUrl;
        this.userName = userName;
        this.ownDesc = ownDesc;
        this.ownType = ownType;
    }

    public String getUserPicUrl() {
        return userPicUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getOwnDesc() {
        return ownDesc;
    }

    public int getOwnType() {
        return ownType;
    }
}
