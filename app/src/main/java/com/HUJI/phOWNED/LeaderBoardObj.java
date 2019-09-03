package com.HUJI.phOWNED;

import java.io.Serializable;

/**
 * this class act as a holder for the leader board, it's hold all the necessary info we
 * need for the leader board
 */
public class LeaderBoardObj implements Serializable {

    private String userUid;
    private String picUrl;
    private String nickName;
    private String bg = "profile_border"; //default settings
    private String onws;

    /**
     * constructor of the holder
     * @param userUid the user ID
     * @param picUrl URL of the user pic
     * @param nickName user nickname in the app
     * @param onws number of owns as a string
     */
    public LeaderBoardObj(String userUid, String picUrl, String nickName, String onws) {
        this.userUid = userUid;
        this.picUrl = picUrl;
        this.nickName = nickName;
        this.onws = onws;
    }

    /**
     * @return the user ID
     */
    public String getUserUid() {
        return userUid;
    }

    /**
     * @return the user pic URL
     */
    public String getPicUrl() {
        return picUrl;
    }

    /**
     * @return the user nickname
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * @return the number of owns as a string
     */
    public String getOnws() {
        return onws;
    }

    /**
     *sets the background of user card in the leader board
     * @param bg the color of the background
     */
    public void setBg(String bg) {
        this.bg = bg;
    }

    /**
     * @return the user background in the leader board
     */
    public String getBg() {
        return bg;
    }
}
