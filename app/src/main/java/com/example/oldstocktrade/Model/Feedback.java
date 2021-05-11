package com.example.oldstocktrade.Model;

public class Feedback {
    String proID;
    String userID;
    String id;

    public String getId() {
        return id;
    }

    public Feedback() {

    }
    public String getProID() {
        return proID;
    }


    public String getUserID() {
        return userID;
    }

    public Feedback(String proID, String userID, String id) {
        this.proID = proID;
        this.userID = userID;
        this.id = id;
    }
}
