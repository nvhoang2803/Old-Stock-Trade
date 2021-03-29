package com.example.oldstocktrade.Model;

public class Wishlist {
    String ProID;
    String UserID;

    public Wishlist(){

    }

    public String getProID() {
        return ProID;
    }

    public void setProID(String proID) {
        ProID = proID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public Wishlist(String proID, String userID) {
        ProID = proID;
        UserID = userID;
    }
}
