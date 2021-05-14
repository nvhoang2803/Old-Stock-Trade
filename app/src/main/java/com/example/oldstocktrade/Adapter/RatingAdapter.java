package com.example.oldstocktrade.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Model.Rating;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder>{
    private Context context;
    private List<Rating> mItems;

    public RatingAdapter(@NonNull Context context, List<Rating> mItems) {
        this.context = context;
        this.mItems = mItems;
    }

    @NonNull
    @Override
    public RatingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feedback_item,parent,false);
        return new RatingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingAdapter.ViewHolder holder, int position) {
        Rating item = mItems.get(position);
        String userid = item.getUserID();

        FirebaseDatabase.getInstance().getReference("Users").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                holder.username.setText(user.getUsername());
                if (user.getImageURL().equals("default"))
                    holder.profile_image.setImageResource(R.mipmap.ic_launcher);
                else Glide.with(context).load(user.getImageURL()).into(holder.profile_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.txt_feedback.setText(item.getFeedback());
        switch (item.getRating()) {
            case 1:
                holder.img_star1.setBackgroundResource(R.drawable.ic_star_full);
                holder.img_star2.setBackgroundResource(R.drawable.ic_star);
                holder.img_star3.setBackgroundResource(R.drawable.ic_star);
                holder.img_star4.setBackgroundResource(R.drawable.ic_star);
                holder.img_star5.setBackgroundResource(R.drawable.ic_star);
                break;
            case 2:
                holder.img_star1.setBackgroundResource(R.drawable.ic_star_full);
                holder.img_star2.setBackgroundResource(R.drawable.ic_star_full);
                holder.img_star3.setBackgroundResource(R.drawable.ic_star);
                holder.img_star4.setBackgroundResource(R.drawable.ic_star);
                holder.img_star5.setBackgroundResource(R.drawable.ic_star);
                break;
            case 3:
                holder.img_star1.setBackgroundResource(R.drawable.ic_star_full);
                holder.img_star2.setBackgroundResource(R.drawable.ic_star_full);
                holder.img_star3.setBackgroundResource(R.drawable.ic_star_full);
                holder.img_star4.setBackgroundResource(R.drawable.ic_star);
                holder.img_star5.setBackgroundResource(R.drawable.ic_star);
                break;
            case 4:
                holder.img_star1.setBackgroundResource(R.drawable.ic_star_full);
                holder.img_star2.setBackgroundResource(R.drawable.ic_star_full);
                holder.img_star3.setBackgroundResource(R.drawable.ic_star_full);
                holder.img_star4.setBackgroundResource(R.drawable.ic_star_full);
                holder.img_star5.setBackgroundResource(R.drawable.ic_star);
                break;
            case 5:
                holder.img_star1.setBackgroundResource(R.drawable.ic_star_full);
                holder.img_star2.setBackgroundResource(R.drawable.ic_star_full);
                holder.img_star3.setBackgroundResource(R.drawable.ic_star_full);
                holder.img_star4.setBackgroundResource(R.drawable.ic_star_full);
                holder.img_star5.setBackgroundResource(R.drawable.ic_star_full);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profile_image;
        private TextView username;
        private TextView txt_feedback;
        private ImageView img_star1;
        private ImageView img_star2;
        private ImageView img_star3;
        private ImageView img_star4;
        private ImageView img_star5;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.ImgID);
            username = itemView.findViewById(R.id.username);
            txt_feedback = itemView.findViewById(R.id.txt_feedback);
            img_star1 = itemView.findViewById(R.id.star1);
            img_star2 = itemView.findViewById(R.id.star2);
            img_star3 = itemView.findViewById(R.id.star3);
            img_star4 = itemView.findViewById(R.id.star4);
            img_star5 = itemView.findViewById(R.id.star5);

        }
    }
}
