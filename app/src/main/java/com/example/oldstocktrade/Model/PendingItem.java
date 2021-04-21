package com.example.oldstocktrade.Model;

import java.util.ArrayList;

public class PendingItem {
    private String proID;
    private int status;
    private ArrayList<String> Users;

    public ArrayList<String> getUsers() {
        return Users;
    }

    public void setUsers(ArrayList<String> users) {
        Users = users;
    }

    public PendingItem(String proID, int status) {
        this.proID = proID;
        this.status = status;
    }

    public PendingItem() {
    }

    public String getProID() {
        return proID;
    }

    public int getStatus() {
        return status;
    }
}
