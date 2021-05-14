package com.example.oldstocktrade.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Model.Feedback;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.R;
import com.example.oldstocktrade.Activity.FeedbackActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder>{
    private Context context;
    private List<Feedback> mItems;
    public FeedbackAdapter(@NonNull Context context, List<Feedback> mItems) {
        this.context = context;
        this.mItems = mItems;
    }

    @NonNull
    @Override
    public FeedbackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rating_item,parent,false);
        return new FeedbackAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackAdapter.ViewHolder holder, int position) {
        Feedback item = mItems.get(position);
        String proID = item.getProID();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products").child(proID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                holder.product_name.setText(product.getName());
                Glide.with(context).load(product.getImageURL().get(0)).into(holder.product_image);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, FeedbackActivity.class);
                        i.putExtra("proID",mItems.get(position).getProID());
                        i.putExtra("userID",mItems.get(position).getUserID());
                        i.putExtra("feedbackID",mItems.get(position).getId());
                        context.startActivity(i);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView product_image;
        private TextView product_name;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            product_image = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.product_name);
        }
    }
}
