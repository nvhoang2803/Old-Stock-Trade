package com.example.oldstocktrade;

import com.example.oldstocktrade.Model.Comment;

import java.util.Comparator;

public class CustomComparator implements Comparator<Comment> {
    @Override
    public int compare(Comment o1, Comment o2) {
        if (o1.getTimestamp() > o2.getTimestamp()){
            return 0;
        }
        return 1;
    }
}