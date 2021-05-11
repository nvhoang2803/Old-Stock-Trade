package com.example.oldstocktrade.Model;

public class Rating {
    private String id;
    private String feedback;
    private int rating;

    public String getId() {
        return id;
    }

    public String getFeedback() {
        return feedback;
    }

    public int getRating() {
        return rating;
    }

    public Rating() {
    }

    public Rating(String id, String feedback, int rating) {
        this.id = id;
        this.feedback = feedback;
        this.rating = rating;
    }
}
