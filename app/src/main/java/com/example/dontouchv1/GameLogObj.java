/**
 * this class is a game log obj of own
 * used in GameLogAdapter
 */
package com.example.dontouchv1;

public class GameLogObj {
    private String userPicUrl;
    private String userName;
    private String ownDesc;
    private int ownType;

    /**
     * own constructor
     * @param userPicUrl owned user pic url
     * @param userName owned user name
     * @param ownDesc own description
     * @param ownType own type
     */
    public GameLogObj(String userPicUrl, String userName, String ownDesc, int ownType) {
        this.userPicUrl = userPicUrl;
        this.userName = userName;
        this.ownDesc = ownDesc;
        this.ownType = ownType;
    }

    /**
     * get owned user pic url
     * @return owned user pic url
     */
    public String getUserPicUrl() {
        return userPicUrl;
    }

    /**
     * get owned user name
     * @return owned user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * get own description
     * @return own description
     */
    public String getOwnDesc() {
        return ownDesc;
    }

    /**
     * get own type
     * @return own type
     */
    public int getOwnType() {
        return ownType;
    }
}
