package com.example.oldstocktrade.Model;

import java.util.ArrayList;

public class Product {
    String Address;
    String Buyer;
    String Description;
    ArrayList<String> ImageURL;
    double Latitude;
    double Longitude;
    String Name;
    double Price;
    String ProID;
    int Report;
    String Seller;
    int Status;
    long Timestamp;
    boolean VisibleToSeller;
    boolean VisibleToBuyer;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getBuyer() {
        return Buyer;
    }

    public void setBuyer(String buyer) {
        Buyer = buyer;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public ArrayList<String> getImageURL() {
        return ImageURL;
    }

    public void setImageURL(ArrayList<String> imageURL) {
        ImageURL = imageURL;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(long Longitude) {
        Longitude = Longitude;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getProID() {
        return ProID;
    }

    public void setProID(String proID) {
        ProID = proID;
    }

    public int getReport() {
        return Report;
    }

    public void setReport(int report) {
        Report = report;
    }

    public String getSeller() {
        return Seller;
    }

    public void setSeller(String seller) {
        Seller = seller;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long timestamp) {
        Timestamp = timestamp;
    }



    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public boolean isVisibleToSeller() {
        return VisibleToSeller;
    }

    public boolean isVisibleToBuyer() {
        return VisibleToBuyer;
    }

    public void setVisibleToSeller(boolean visibleToSeller) {
        VisibleToSeller = visibleToSeller;
    }

    public void setVisibleToBuyer(boolean visibleToBuyer) {
        VisibleToBuyer = visibleToBuyer;
    }


    public Product(){

    }

    public Product(String address, String buyer, String description, ArrayList<String> imageURL, double latitude, double Longitude, String name, double price, String proID, int report,
                   String seller, int status, long timestamp, String userImageURL, float rate) {
        Address = address;
        Buyer = buyer;
        Description = description;
        ImageURL = imageURL;
        Latitude = latitude;
        Longitude = Longitude;
        Name = name;
        Price = price;
        ProID = proID;
        Report = report;
        Seller = seller;
        Status = status;
        Timestamp = timestamp;
        this.rate = rate;
        VisibleToSeller = true;
        VisibleToBuyer = true;
    }

    float rate;


}
