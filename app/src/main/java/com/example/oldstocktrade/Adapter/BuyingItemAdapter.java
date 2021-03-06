package com.example.oldstocktrade.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Model.BuyingItem;
import com.example.oldstocktrade.Model.Contact;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


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
        int status = item.getStatus();
        String proID = item.getProID();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products").child(proID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                holder.product_name.setText(product.getName());
                holder.product_price.setText(product.getPrice()+" $");
                Glide.with(context).load(product.getImageURL().get(0)).into(holder.product_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        switch (status){
            case 0:
                holder.status0.setVisibility(View.VISIBLE);
                break;
            case 1:
                holder.status1.setVisibility(View.VISIBLE);
                break;
            case 2:
                holder.status2.setVisibility(View.VISIBLE);
                break;
            case 3:
                holder.status3.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView product_image;
        private TextView product_name;
        private TextView product_price;
        private TextView status0;
        private TextView status1;
        private TextView status2;
        private LinearLayout status3;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            product_image = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            status0 = itemView.findViewById(R.id.status0);
            status1 = itemView.findViewById(R.id.status1);
            status2 = itemView.findViewById(R.id.status2);
            status3 = itemView.findViewById(R.id.status3);
        }
    }
}
