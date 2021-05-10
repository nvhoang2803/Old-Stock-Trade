package com.example.oldstocktrade.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.MessageActivity;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    Context context;
    List<User> mUsers;
    List<Long> min_time;
    List<String> last_message;
    private List<Boolean> mIsSeen;
    //List<RecentMessage> mMessages;
    public ContactAdapter(@NonNull Context context, List<User> mUsers, List<Long> min_time, List<String> last_message, List<Boolean> mIsSeen) {
        this.context = context;
        this.mUsers = mUsers;
        this.min_time = min_time;
        this.last_message = last_message;
        this.mIsSeen = mIsSeen;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_contact,parent,false);
        return new ContactAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default"))
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        else Glide.with(context).load(user.getImageURL()).into(holder.profile_image);

        if (user.getStatus().equals("offline")){
            holder.img_off.setVisibility(View.VISIBLE);
            holder.img_on.setVisibility(View.GONE);
        } else {
            holder.img_on.setVisibility(View.VISIBLE);
            holder.img_off.setVisibility(View.GONE);
        }
        if (last_message != null && min_time != null){
            holder.last_msg.setText(last_message.get(position));
            long time = System.currentTimeMillis() - min_time.get(position);
            String time_duration = "";
            time = time /1000;
            if (time / (60* 60 * 24) > 0){
                time_duration = time / (60* 60 * 24) + " days ago";
            }else if (time / (60* 60) > 0){
                time_duration = time / (60* 60) + " hours ago";
            }else if ((time / 60) > 0){
                time_duration = time / (60) + " mins ago";
            }
            else time_duration += "Just now";
            holder.sent_time.setText(time_duration);
            if (!mIsSeen.get(position)){
                holder.last_msg.setTextColor(Color.WHITE);
                holder.img_new.setVisibility(View.VISIBLE);
            }
            else {
                holder.last_msg.setTextColor(R.color.recent_message_color);
                holder.img_new.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class).addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                intent.putExtra("userid",user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView profile_image;
        public ImageView img_on;
        public ImageView img_off;
        public TextView last_msg;
        public TextView sent_time;
        public CircleImageView img_new;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.ImgID);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            sent_time = itemView.findViewById(R.id.sent_time);
            img_new = itemView.findViewById(R.id.img_new);
        }
    }
}
