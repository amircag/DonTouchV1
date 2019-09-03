package com.HUJI.phOWNED;

/**
 * this class acts as an holder to the OWNS. each OWN will be hold in an instance of this class.
 */
public class OwnLogObj {

    private String ownDisc;
    private String ownPic;

    public String getOwnDisc() {
        return ownDisc;
    }

    public String getOwnPic() {
        return ownPic;
    }

    /**
     * this constructor convert the own type from the database to a format that can display the
     * type image
     * @param ownDisc the description of the OWN
     * @param ownPic the OWN type
     */
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
