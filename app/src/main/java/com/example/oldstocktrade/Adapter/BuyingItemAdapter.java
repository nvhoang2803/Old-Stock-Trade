package com.example.oldstocktrade.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oldstocktrade.Model.BuyingItem;
import com.example.oldstocktrade.Model.Contact;
import com.example.oldstocktrade.R;

import java.util.List;


public class BuyingItemAdapter extends RecyclerView.Adapter<BuyingItemAdapter.ViewHolder>{
    private Context context;
    private List<BuyingItem> mItems;
    public BuyingItemAdapter(@NonNull Context context, List<BuyingItem> mItems) {
        this.context = context;
        this.mItems = mItems;
    }

    @NonNull
    @Override
    public BuyingItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.buying_item,parent,false);
        return new BuyingItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyingItemAdapter.ViewHolder holder, int position) {
        BuyingItem item = mItems.get(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
