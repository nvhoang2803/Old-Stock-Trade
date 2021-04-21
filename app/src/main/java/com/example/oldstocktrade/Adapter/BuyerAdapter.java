package com.example.oldstocktrade.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.MessageActivity;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Model.User;
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

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyerAdapter extends RecyclerView.Adapter<BuyerAdapter.MyViewHolder> {
    Context mContext;
    List<String> mUsers;
    RecyclerView mRecyclerView;


    public BuyerAdapter(Context mContext, List<String> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public BuyerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.buyer_item,parent,false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class MyViewHolder  extends RecyclerView.ViewHolder{
        private CircleImageView user_image;
        private TextView username;
        private ImageButton btn_confirm;
        private ImageButton btn_delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            user_image = itemView.findViewById(R.id.user_image);
            username = itemView.findViewById(R.id.username);
            btn_confirm = itemView.findViewById(R.id.btn_confirm);
            btn_delete = itemView.findViewById(R.id.btn_delete);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull BuyerAdapter.MyViewHolder holder, int position) {
        String userID = mUsers.get(position);
        FirebaseDatabase.getInstance().getReference().child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                holder.username.setText(user.getUsername());
                if (user.getImageURL().equals("default"))
                    holder.user_image.setImageResource(R.mipmap.ic_launcher);
                else Glide.with(mContext).load(user.getImageURL()).into(holder.user_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
