package com.example.oldstocktrade.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    public ListViewAdapter(ArrayList<Product> productArrayList) {
        this.productArrayList = productArrayList;
    }

    ArrayList<Product> productArrayList;

    @Override
    public int getCount() {
        return productArrayList.size();
    }

    @Override
    public Product getItem(int position) {
        return productArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View productView;
        TextView userName;
        ImageView userImage;
        ImageView productImage;

        if (convertView == null){
            productView = View.inflate(parent.getContext(), R.layout.product_view ,null);
        }else{
            productView = convertView;
        }

        userName = productView.findViewById(R.id.userName);
        userImage = productView.findViewById(R.id.userImage);
        productImage = productView.findViewById(R.id.productImage);
        userName.setText(productArrayList.get(position).getName());

        userImage.setImageResource(R.mipmap.ic_launcher_foreground);
        productImage.setImageResource(R.mipmap.ic_launcher_foreground);
        return productView;
    }

    public void filter(int price,int distance, int rating){


    }
}
