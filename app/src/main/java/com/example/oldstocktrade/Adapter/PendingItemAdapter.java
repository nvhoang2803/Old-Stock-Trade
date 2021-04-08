package com.example.oldstocktrade.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Model.BuyingItem;
import com.example.oldstocktrade.Model.Contact;
import com.example.oldstocktrade.Model.PendingItem;
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


public class PendingItemAdapter extends RecyclerView.Adapter<PendingItemAdapter.ViewHolder>{
    private Context context;
    private List<PendingItem> mItems;
    public PendingItemAdapter(@NonNull Context context, List<PendingItem> mItems) {
        this.context = context;
        this.mItems = mItems;
    }

    @NonNull
    @Override
    public PendingItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pending_item,parent,false);
        return new PendingItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingItemAdapter.ViewHolder holder, int position) {
        PendingItem item = mItems.get(position);
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
                holder.product_detail.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        switch (status){
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
        //private TextView status0;
        private RecyclerView status1;
        private LinearLayout status2;
        private TextView status3;
        private RelativeLayout product_detail;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            product_image = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            status1 = itemView.findViewById(R.id.status1);
            status2 = itemView.findViewById(R.id.status2);
            status3 = itemView.findViewById(R.id.status3);
            product_detail = itemView.findViewById(R.id.product_detail);
        }
    }
}
