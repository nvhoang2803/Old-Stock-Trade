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
import com.example.oldstocktrade.FullscreenActivity;
import com.example.oldstocktrade.Model.Chat;
import com.example.oldstocktrade.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<Chat> mChats;
    String imageURL;

    public MessageAdapter(@NonNull Context context, List<Chat> mChats, String imageURL) {
        this.context = context;
        this.mChats = mChats;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.message_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.message_left,parent,false);
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChats.get(position);
        if (imageURL.equals("default"))
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        else Glide.with(context).load(imageURL).into(holder.profile_image);
        if (chat.getType().equals("text")){
            holder.message.setText(chat.getMessage());
            holder.message.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
        }
        else if (chat.getType().equals("image")){
            Glide.with(context).load(chat.getMessage()).into(holder.image);
            holder.message.setVisibility(View.GONE);
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, FullscreenActivity.class);
                    i.putExtra("image", chat.getMessage());
                    context.startActivity(i);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public CircleImageView profile_image;
        public ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.message);
            profile_image = itemView.findViewById(R.id.profile_image);
            image = itemView.findViewById(R.id.imageMsg);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fuser.getUid().equals(mChats.get(position).getSender()))
            return MSG_TYPE_RIGHT;
        else return MSG_TYPE_LEFT;
        //return super.getItemViewType(position);
    }
}
