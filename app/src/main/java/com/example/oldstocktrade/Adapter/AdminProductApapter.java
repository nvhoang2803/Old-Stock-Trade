package com.example.oldstocktrade.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminProductApapter extends RecyclerView.Adapter<AdminProductApapter.ViewHolder> {

    Activity curActivity;
    User tmp;

    public AdminProductApapter(Activity curActivity, ArrayList<Product> productArrayList) {
        this.curActivity = curActivity;
        this.productArrayList = productArrayList;
    }

    ArrayList<Product> productArrayList;
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    StorageReference sr= FirebaseStorage.getInstance().getReference();
    ArrayList<Double> lonlat;
    String[] arrImage;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(curActivity).inflate(R.layout.admin_pview,parent,false);
        return new AdminProductApapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product tmp = productArrayList.get(position);
        holder.productName.setText(tmp.getName());
        long time = System.currentTimeMillis() - productArrayList.get(position).getTimestamp();
        String timeD = "";
        time = time /1000;
        if (!(time / (60* 60 * 24 * 5) > 0)){
            if (time / (60* 60 * 24) > 0){
                timeD = (int) ( time / (60* 60 * 24)) + " days";
            }else if (time / (60 * 60 ) > 0){
                timeD =(int) ( time / (60* 60)) + " hours";
            }else{
                timeD = (int) (time / (60)) + " mins";
            }
        }else{
            timeD = new SimpleDateFormat("yyyy-MM-dd").format(new Date(productArrayList.get(position).getTimestamp()));
        }
        holder.productDes.setText(timeD + "-- Report :" + tmp.getReport());
        holder.productManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.product_spContainer.getVisibility() == View.GONE){
                    Glide.with(holder.productImage).load(productArrayList.get(position).getImageURL().get(0)).into(holder.productImage);
                    holder.product_spName.setText(productArrayList.get(position).getName());
                    holder.product_spDes.setText(productArrayList.get(position).getDescription());
                    if (productArrayList.get(position).isEnable()){
                        holder.product_spStatus.setText("OnBoard");
                    }else {
                        holder.product_spStatus.setText("Blocked");
                    }
                    if (productArrayList.get(position).isEnable()){
                        holder.productBlock.setText("BLOCK");
                    }else{
                        holder.productBlock.setText("UNBLOCK");
                    }
                    holder.productBlock.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Map<String, Object> childUpdates = new HashMap<>();
                            if (holder.productBlock.getText() == "BLOCK"){
                                childUpdates.put("Enable", false);
                                mReference.child("Products").child(productArrayList.get(position).getProID()).updateChildren(childUpdates);
                                holder.productBlock.setText("UNBLOCK");
                            }else{
                                childUpdates.put("Enable", true);
                                mReference.child("Products").child(productArrayList.get(position).getProID()).updateChildren(childUpdates);
                                holder.productBlock.setText("BLOCK");
                            }
                        }
                    });

                    holder.productRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mReference.child("Products").child(productArrayList.get(position).getProID()).removeValue();
                        }
                    });

                    holder.product_spContainer.setVisibility(View.VISIBLE);
                    holder.product_spContainer.bringToFront();

                }
                else{
                    holder.product_spContainer.setVisibility(View.GONE);
                }
            }
        });

        if (productArrayList.get(position).getReport() > 10){
            holder.itemView.setBackgroundColor(Color.rgb(226, 11, 11));
        }else{
            holder.itemView.setBackgroundColor(Color.rgb(255, 255, 255));
        }

    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productDes;
        Button productManage;
        ConstraintLayout product_spContainer;
        ImageView productImage;
        TextView product_spName;
        TextView product_spDes;
        TextView product_spStatus;
        Button productBlock;
        Button productRemove;
        ConstraintLayout admin_producContainer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.admin_productname);
            productDes = itemView.findViewById(R.id.admin_productdes);
            productManage = itemView.findViewById(R.id.admin_productbtnManage);
            product_spContainer = itemView.findViewById(R.id.admin_product_spContainer);
            productImage = itemView.findViewById(R.id.admin_product_spImage);
            product_spName = itemView.findViewById(R.id.admin_product_spName);
            product_spDes = itemView.findViewById(R.id.admin_product_spDes);
            product_spStatus = itemView.findViewById(R.id.admin_product_spStatus);
            productBlock = itemView.findViewById(R.id.admin_product_spbtnBlock);
            productRemove = itemView.findViewById(R.id.admin_product_spbtnRemove);
            admin_producContainer= itemView.findViewById(R.id.admin_producContainer);
        }
    }

}
