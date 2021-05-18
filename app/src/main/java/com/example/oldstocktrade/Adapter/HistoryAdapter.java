package com.example.oldstocktrade.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Activity.MainActivity;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Activity.ParticularPageActivity;
import com.example.oldstocktrade.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    Context mContext;
    List<Product> mData;
    String tabName;
    RecyclerView mRecyclerView;
    Activity curActivity;

    public HistoryAdapter(Context mContext, Activity curActivity, List<Product> mData, String tabName) {
        this.mContext = mContext;
        this.mData = mData;
        this.tabName = tabName;
        this.curActivity = curActivity;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.history_item,parent,false);
//        parentView = LayoutInflater.from(mContext).inflate(R.layout.fragment_sold,parent,false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position,boolean isLongClick);
    }

    public static class MyViewHolder  extends RecyclerView.ViewHolder{
        private ImageView proImage;
        private TextView proName;
        private TextView proDate;
        private TextView proPrice;
        private ImageView btnDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            proImage = (ImageView) itemView.findViewById(R.id.proImage);
            proName = (TextView) itemView.findViewById(R.id.proName);
            proDate = (TextView) itemView.findViewById(R.id.proTransactedDate);
            proPrice = (TextView) itemView.findViewById(R.id.proPrice);
            btnDelete = (ImageView) itemView.findViewById(R.id.btn_historydelete);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.MyViewHolder holder, int position) {
        holder.proName.setText(mData.get(position).getName());
        holder.proPrice.setText(Long.toString(Math.round(mData.get(position).getPrice())));
        Date date = new Date(mData.get(position).getTimestamp());
        holder.proDate.setText(DateFormat.getDateInstance().format(date));
        if (mData.get(position).getImageURL() != null){
            Glide.with(holder.proImage).load(mData.get(position).getImageURL().get(0))
                    .into(holder.proImage);
        }else {
            Glide.with(holder.proImage).load("https://st3.depositphotos.com/23594922/31822/v/600/depositphotos_318221368-stock-illustration-missing-picture-page-for-website.jpg")
                    .into(holder.proImage);
        }
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Products");
                if (tabName.equals("bought")){
                    mData.get(position).setVisibleToBuyer(false);
                    mReference.child(mData.get(position).getProID()).child("VisibleToBuyer").setValue(false);
                }
                else {
                    mData.get(position).setVisibleToSeller(false);
                    mReference.child(mData.get(position).getProID()).child("VisibleToSeller").setValue(false);
                }
                mData.remove(position);
                mRecyclerView.removeViewAt(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mData.size());
            }
        }
        );

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id= mData.get(position).getProID();
                String userID= mData.get(position).getSeller();
                Intent partIntent= new Intent(v.getContext(), ParticularPageActivity.class);
                partIntent.putExtra("id", id);
                partIntent.putExtra("userID", userID);
                partIntent.putExtra("sizeImageURL", mData.get(position).getImageURL().size());
                partIntent.putExtra("longitude", ((MainActivity) curActivity).longitude);
                partIntent.putExtra("latitude",((MainActivity) curActivity).latitude);
                partIntent.putExtra("address",((MainActivity) curActivity).address);
                partIntent.putExtra("sumReport", mData.get(position).getReport());
                Log.d("PackInHomeView", "onClick: ");
                v.getContext().startActivity(partIntent);
            }
        });
    }
}
