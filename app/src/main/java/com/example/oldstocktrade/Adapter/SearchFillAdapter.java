package com.example.oldstocktrade.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.oldstocktrade.R;

import java.util.ArrayList;

public class SearchFillAdapter extends BaseAdapter {
    protected ArrayList<String> currentView;
    protected ArrayList<String> allListView;
    TextView name;

    public SearchFillAdapter(ArrayList<String> allListView) {
        this.currentView = (ArrayList<String>) allListView.clone();
        this.allListView = (ArrayList<String>) allListView.clone();
    }

    @Override
    public int getCount() {
        return currentView.size();
    }

    @Override
    public Object getItem(int position) {
        return currentView.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null){
            view = View.inflate(parent.getContext(), R.layout.search_view,null);
        }else{
            view = convertView;
        }
        name = view.findViewById(R.id.search_item_name);
        name.setText(currentView.get(position));
        return view;
    }

    public void filter(String text){
        text = text.toLowerCase();
        this.currentView.clear();

        for (int i=0;i<this.allListView.size();i++){
            if (this.allListView.get(i).toLowerCase().contains(text)){
                this.currentView.add(this.allListView.get(i));
            }
        }
    }
}
