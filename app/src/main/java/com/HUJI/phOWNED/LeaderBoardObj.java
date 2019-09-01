package com.HUJI.phOWNED;

import java.io.Serializable;

public class LeaderBoardObj implements Serializable {

    private String userUid;
    private String picUrl;
    private String nickName;
    private String bg = "profile_border"; //default settings
    private String onws;

    public LeaderBoardObj(String userUid, String picUrl, String nickName, String onws) {
        this.userUid = userUid;
        this.picUrl = picUrl;
        this.nickName = nickName;
        this.onws = onws;
    }

    public String getUserUid() {
        return userUid;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public String getOnws() {
        return onws;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String getBg() {
        return bg;
    }
}
