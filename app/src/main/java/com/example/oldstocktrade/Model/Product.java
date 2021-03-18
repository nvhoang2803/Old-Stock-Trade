package com.example.oldstocktrade.Model;

public class Product {
    String name;
    float rate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getRate() {
        return rate;
    }

    public Product(String name, float rate) {
        this.name = name;
        this.rate = rate;
    }
}
