package com.example.dontouchv1;

public class GroupObj {

    private String groupName;
    private String groupPic;
    private String firstPlaceId;
    private String lastPlaceId;
    private String firstPlaceName;
    private String firstPlacePic;
    private String lastPlaceName;
    private String lastPlacePic;
    private String groupId;
    private boolean activeGame;

    public boolean isActiveGame() {
        return activeGame;
    }

    public void setActiveGame(boolean activeGame) {
        this.activeGame = activeGame;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getFirstPlaceName() {
        return firstPlaceName;
    }

    public void setFirstPlaceName(String firstPlaceName) {
        this.firstPlaceName = firstPlaceName;
    }

    public String getFirstPlacePic() {
        return firstPlacePic;
    }

    public void setFirstPlacePic(String firstPlacePic) {
        this.firstPlacePic = firstPlacePic;
    }

    public String getLastPlaceName() {
        return lastPlaceName;
    }

    public void setLastPlaceName(String lastPlaceName) {
        this.lastPlaceName = lastPlaceName;
    }

    public String getLastPlacePic() {
        return lastPlacePic;
    }

    public void setLastPlacePic(String lastPlacePic) {
        this.lastPlacePic = lastPlacePic;
    }

    public GroupObj(String groupName, String groupPic, String firstPlaceId, String lastPlaceId) {
        this.groupName = groupName;
        this.groupPic = groupPic;
        this.firstPlaceId = firstPlaceId;
        this.lastPlaceId = lastPlaceId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupPic() {
        return groupPic;
    }

    public String getFirstPlaceId() {
        return firstPlaceId;
    }

    public String getLastPlaceId() {
        return lastPlaceId;
    }
}
