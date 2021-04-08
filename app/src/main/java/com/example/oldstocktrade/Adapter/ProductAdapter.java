package com.example.oldstocktrade.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.MessageActivity;
import com.example.oldstocktrade.Model.BuyingItem;
import com.example.oldstocktrade.Model.Chat;
import com.example.oldstocktrade.Model.PendingItem;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Model.WishListItem;
import com.example.oldstocktrade.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    Context mContext;
    List<Product> mData;
    RecyclerView mRecyclerView;


    public ProductAdapter(Context mContext, List<Product> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.product_item,parent,false);
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
        private ImageView btn_buy;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            proImage = (ImageView) itemView.findViewById(R.id.proImage);
            proName = (TextView) itemView.findViewById(R.id.proName);
            proDate = (TextView) itemView.findViewById(R.id.proTransactedDate);
            proPrice = (TextView) itemView.findViewById(R.id.proPrice);
            btn_buy = itemView.findViewById(R.id.btn_buy);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.MyViewHolder holder, int position) {
        holder.proName.setText(mData.get(position).getName());
        holder.proPrice.setText(Long.toString(Math.round(mData.get(position).getPrice())));
        Date date = new Date(mData.get(position).getTimestamp());
        holder.proDate.setText(DateFormat.getDateInstance().format(date));
        Glide.with(holder.proImage).load(mData.get(position).getImageURL().get(0))
                .into(holder.proImage);
        holder.btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setTitle("Confirm dialog")
                        .setMessage("Do you want to buy this product?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

                                String proID = mData.get(position).getProID();
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Buying").child(proID);
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("proID",proID);
                                hashMap.put("status",1);
                                ref.setValue(hashMap);
                                String sellerID = mData.get(position).getSeller();
                                final DatabaseReference ref_pending = FirebaseDatabase.getInstance().getReference("Users").child(sellerID).child("Pending").child(proID);
                                ref_pending.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        PendingItem item = snapshot.getValue(PendingItem.class);

                                        if (item != null){
                                            ref_pending.child("Users").child(Integer.toString(item.getUsers().size())).setValue(fuser.getUid());
                                        }
                                        else {
                                            HashMap<String,Object> hashMap1 = new HashMap<>();
                                            hashMap1.put("status",1);
                                            hashMap1.put("proID",proID);
                                            ref_pending.setValue(hashMap1);
                                            ref_pending.child("Users").child(Integer.toString(0)).setValue(fuser.getUid());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        })
                        .setNegativeButton("No",null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });

    }

}
