package com.example.oldstocktrade.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    Context mContext;
    List<Product> mData;
    RecyclerView mRecyclerView;
    boolean isHidden;
    String userID;


    public ProductAdapter(Context mContext, List<Product> mData, boolean isHidden,String userID) {
        this.mContext = mContext;
        this.mData = mData;
        this.isHidden = isHidden;
        this.userID = userID;
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
            btn_buy.setVisibility(View.GONE);
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
        if (!isHidden) {
            holder.btn_buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(mContext)
                            .setTitle("Confirm dialog")
                            .setMessage("Do you want to sell this product?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Products").child(mData.get(position).getProID());
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("Buyer", userID);
                                    hashMap.put("Status", 0);
                                    hashMap.put("Timestamp", System.currentTimeMillis());
                                    ref.updateChildren(hashMap);
                                    DatabaseReference ref_feedback = FirebaseDatabase.getInstance().getReference("Feedback").child(userID).push();
                                    HashMap<String,Object> hashMap1 = new HashMap<>();
                                    hashMap1.put("id",ref_feedback.getKey());
                                    hashMap1.put("userID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap1.put("proID",mData.get(position).getProID());
                                    ref_feedback.updateChildren(hashMap1);
                                    Toast.makeText(mContext,"Sold successfully",Toast.LENGTH_SHORT).show();
                                    ((Activity) mContext).finish();
                                }
                            })
                            .setNegativeButton("No",null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            });
            holder.btn_buy.setVisibility(View.VISIBLE);
        }


    }

}
