package com.example.oldstocktrade.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.BoughtFragment;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    Context mContext;
    List<Product> mData;
    String tabName;
    RecyclerView mRecyclerView;


    public RecyclerViewAdapter(Context mContext, List<Product> mData, String tabName) {
        this.mContext = mContext;
        this.mData = mData;
        this.tabName = tabName;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.history_item,parent,false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }


    @Override
    public int getItemCount() {
        return mData.size();
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
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.proName.setText(mData.get(position).getName());
        holder.proPrice.setText(Long.toString(Math.round(mData.get(position).getPrice())));
        Date date = new Date(mData.get(position).getTimestamp());
        holder.proDate.setText(DateFormat.getDateInstance().format(date));
        Glide.with(holder.proImage).load(mData.get(position).getImageURL())
                .into(holder.proImage);
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
    }
}
