package com.example.dontouchv1;

public class OwnLogObj {

    private String ownDisc;
    private String ownPic;

    public String getOwnDisc() {
        return ownDisc;
    }

    public String getOwnPic() {
        return ownPic;
    }

    public OwnLogObj(String ownDisc, Long ownPic) {
        this.ownDisc = ownDisc;
        if (ownPic == 1) {
            this.ownPic = "beer";
        }
        if (ownPic == 2) {
            this.ownPic = "running";
        } else {
            this.ownPic = "conversation";

        }
    }
}
