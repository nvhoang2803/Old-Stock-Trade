package com.example.oldstocktrade.Model;

public class Comment {
    String Content;
    String ID;
    String ProID;
    long Timestamp;
    String UserID;

    public Comment(){

    }

    public Comment(String content, String ID, String proID, long timestamp, String userID) {
        Content = content;
        this.ID = ID;
        ProID = proID;
        Timestamp = timestamp;
        UserID = userID;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getProID() {
        return ProID;
    }

    public void setProID(String proID) {
        ProID = proID;
    }

    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long timestamp) {
        Timestamp = timestamp;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
