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

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    Activity curActivity;
    User tmp;

    public AdminUserAdapter(Activity curActivity, ArrayList<User> userArrayList) {
        this.curActivity = curActivity;
        this.userArrayList = userArrayList;
    }

    ArrayList<User> userArrayList;
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    StorageReference sr= FirebaseStorage.getInstance().getReference();
    ArrayList<Double> lonlat;
    String[] arrImage;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(curActivity).inflate(R.layout.admin_uview,parent,false);
        return new AdminUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User tmp = userArrayList.get(position);

        if (tmp.getStatus() != null){
            holder.userName.setText(tmp.getUsername());
        }
        if (tmp.getStatus() != null){
            holder.userDes.setText(tmp.getStatus());
        }else{
            holder.userDes.setText("UNKNOWN");
        }
        //        holder.productDes.setText(timeD + "-- Report :" + tmp.getReport());
        holder.productManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.product_Container.getVisibility() == View.GONE){
                    Glide.with(holder.productImage).load(userArrayList.get(position).getImageURL()).into(holder.productImage);
                    holder.product_spName.setText(userArrayList.get(position).getUsername());
                    holder.product_spDes.setText(userArrayList.get(position).getAddress());
                    holder.product_spStatus.setText("Phone :" + userArrayList.get(position).getPhone());

                    if (holder.productBlock.getText() == ""){
                        if (userArrayList.get(position).isEnable()){
                            holder.productBlock.setText("BLOCK");
                        }else{
                            holder.productBlock.setText("UNBLOCK");
                        }
                    }
                    holder.productBlock.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Map<String, Object> childUpdates = new HashMap<>();
                            if (holder.productBlock.getText() == "BLOCK"){
                                childUpdates.put("Enable", false);
                                mReference.child("Users").child(userArrayList.get(position).getId()).updateChildren(childUpdates);
                                holder.productBlock.setText("UNBLOCK");
                            }else{
                                childUpdates.put("Enable", true);
                                mReference.child("Users").child(userArrayList.get(position).getId()).updateChildren(childUpdates);
                                holder.productBlock.setText("BLOCK");
                            }
                        }
                    });

                    holder.product_Container.setVisibility(View.VISIBLE);
                    holder.product_Container.bringToFront();

                }
                else{
                    holder.product_Container.setVisibility(View.GONE);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView userDes;
        Button productManage;
        ImageView productImage;
        TextView product_spName;
        TextView product_spDes;
        TextView product_spStatus;
        Button productBlock;
//        Button productRemove;
        ConstraintLayout product_Container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.admin_userName);
            userDes = itemView.findViewById(R.id.admin_userDes);
            productManage = itemView.findViewById(R.id.admin_userbtnManage);
            productImage = itemView.findViewById(R.id.admin_user_spImage);
            product_spName = itemView.findViewById(R.id.admin_user_spName);
            product_spDes = itemView.findViewById(R.id.admin_user_spDes);
            product_spStatus = itemView.findViewById(R.id.admin_user_spStatus);
            product_Container = itemView.findViewById(R.id.admin_user_spContainer);
            productBlock = itemView.findViewById(R.id.admin_user_spbtnBlock);
//            productRemove = itemView.findViewById(R.id.admin_product_spbtnRemove);
//            admin_producContainer= itemView.findViewById(R.id.admin_producContainer);
        }
    }

}
