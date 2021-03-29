package com.example.oldstocktrade.Model;

public class WishListItem {
    private String userID;
    private String proID;

    public String getUserID() {
        return userID;
    }

    public WishListItem() {
    }

    public String getProID() {
        return proID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setProID(String proID) {
        this.proID = proID;
    }

    public WishListItem(String userID, String proID) {
        this.userID = userID;
        this.proID = proID;
    }
}
