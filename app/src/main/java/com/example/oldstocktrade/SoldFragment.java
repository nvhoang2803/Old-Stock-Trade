package com.example.oldstocktrade;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.oldstocktrade.Model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class SoldFragment extends Fragment {
    View v;
    private RecyclerView myrecycleview;
    private List<Product> lstProduct;

    public SoldFragment(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        v = inflater.inflate(R.layout.fragment_sold, container, false);
        return v;
    }

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        lstProduct = new ArrayList<>();
        lstProduct.add(new Product("257 Ba Thang Hai Street, HCM city","None","Broaden your Smart TV experience with the LG TV...","https://firebasestorage.googleapis.com/v0/b/old-stock-trade.appspot.com/o/Post%2F1616069897582-jpg?alt=media&token=ba6e44f5-3109-48f8-acc4-12257f05bf2f",0,50000,1,0,"vy",1,"Broaden your Smart TV experience with the LG TV...","https://firebasestorage.googleapis.com/v0/b/old-stock-trade.appspot.com/o/Post%2F1616069897582-jpg?alt=media&token=ba6e44f5-3109-48f8-acc4-12257f05bf2f",5));
        lstProduct.add(new Product("257 Ba Thang Hai Street, HCM city","None","Broaden your Smart TV experience with the LG TV...","https://firebasestorage.googleapis.com/v0/b/old-stock-trade.appspot.com/o/Post%2F1616069897582-jpg?alt=media&token=ba6e44f5-3109-48f8-acc4-12257f05bf2f",0,50000,1,0,"vy",1,"Broaden your Smart TV experience with the LG TV...","https://firebasestorage.googleapis.com/v0/b/old-stock-trade.appspot.com/o/Post%2F1616069897582-jpg?alt=media&token=ba6e44f5-3109-48f8-acc4-12257f05bf2f",5));
        lstProduct.add(new Product("257 Ba Thang Hai Street, HCM city","None","Broaden your Smart TV experience with the LG TV...","https://firebasestorage.googleapis.com/v0/b/old-stock-trade.appspot.com/o/Post%2F1616069897582-jpg?alt=media&token=ba6e44f5-3109-48f8-acc4-12257f05bf2f",0,50000,1,0,"vy",1,,"Broaden your Smart TV experience with the LG TV...","https://firebasestorage.googleapis.com/v0/b/old-stock-trade.appspot.com/o/Post%2F1616069897582-jpg?alt=media&token=ba6e44f5-3109-48f8-acc4-12257f05bf2f",5));
    }


}