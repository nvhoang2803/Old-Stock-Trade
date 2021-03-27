package com.example.oldstocktrade.Model;

public class MyProduct {
    String ProID;
    String UserID;

    public MyProduct(){

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

    public MyProduct(String proID, String userID) {
        ProID = proID;
        UserID = userID;
    }
}
