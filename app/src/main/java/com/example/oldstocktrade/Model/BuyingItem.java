package com.example.oldstocktrade.Model;

public class BuyingItem {
    private String proID;
    private int status;

    public BuyingItem(String proID, int status) {
        this.proID = proID;
        this.status = status;
    }

    public String getProID() {
        return proID;
    }

    public int getStatus() {
        return status;
    }
}
