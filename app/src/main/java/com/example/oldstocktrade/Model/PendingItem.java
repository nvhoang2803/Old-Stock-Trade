package com.example.oldstocktrade.Model;

import java.util.ArrayList;

public class PendingItem {
    private String proID;
    private int status;
    private ArrayList<String> Users;

    public ArrayList<String> getUsers() {
        return Users;
    }

    public PendingItem(String proID, int status, ArrayList<String> Users) {
        this.proID = proID;
        this.status = status;
        this.Users = Users;
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
