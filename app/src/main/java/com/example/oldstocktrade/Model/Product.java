package com.example.oldstocktrade.Model;

public class Product {
    String Address;
    String Buyer;
    String Description;
    String ImageURL;
    long Latitude;
    long Longtitude;
    String Name;
    double Price;
    String ProID;
    int Report;
    String Seller;
    int Status;
    long Timestamp;
    String userImageURL;
    boolean VisibleToSeller = true;
    boolean VisibleToBuyer = true;

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

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public long getLatitude() {
        return Latitude;
    }

    public void setLatitude(long latitude) {
        Latitude = latitude;
    }

    public long getLongtitude() {
        return Longtitude;
    }

    public void setLongtitude(long longtitude) {
        Longtitude = longtitude;
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

    public String getUserImageURL() {
        return userImageURL;
    }

    public void setUserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
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

    public Product(){

    }

    public Product(String address, String buyer, String description, String imageURL, long latitude, long longtitude, String name, double price, String proID, int report, String seller, int status, long timestamp, String userImageURL, float rate) {
        Address = address;
        Buyer = buyer;
        Description = description;
        ImageURL = imageURL;
        Latitude = latitude;
        Longtitude = longtitude;
        Name = name;
        Price = price;
        ProID = proID;
        Report = report;
        Seller = seller;
        Status = status;
        Timestamp = timestamp;
        this.userImageURL = userImageURL;
        this.rate = rate;
        VisibleToSeller = true;
        VisibleToBuyer = true;
    }

    float rate;


}
