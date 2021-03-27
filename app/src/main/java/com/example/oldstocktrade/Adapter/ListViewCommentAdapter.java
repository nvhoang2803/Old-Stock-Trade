package com.example.oldstocktrade.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Model.Comment;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListViewCommentAdapter extends RecyclerView.Adapter<ListViewCommentAdapter.ViewHolder> {
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<Comment> arr;
    Context context;

    public ListViewCommentAdapter(ArrayList<Comment> arrComment,Context context) {
        this.arr = arrComment;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_view,parent,false);
        return new ListViewCommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mReference.child("Users").orderByChild("id").equalTo(arr.get(position).getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            User tmp = ds.getValue(User.class);

                            holder.commentUsername.setText(tmp.getUsername());
                            Glide.with( holder.userImage).load(tmp.getImageURL())
                                    .into( holder.userImage);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.commentContent.setText(arr.get(position).getContent());
        long time = System.currentTimeMillis() - arr.get(position).getTimestamp();
        String timeD = "";
        time = time /1000;
        if (time / (60* 60 * 24) > 0){
            timeD = time / (60* 60 * 24) + " days";
        }else if (time / (60* 60) > 0){
            timeD = time / (60* 60) + " hours";
        }else{
            timeD = time / (60) + " min";
        }
        holder.commentTime.setText(timeD);
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView commentUsername;
        TextView commentContent;
        TextView commentTime;
        ImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            commentTime = itemView.findViewById(R.id.comment_view_time);
            commentUsername = itemView.findViewById(R.id.comment_view_username);
            commentContent = itemView.findViewById(R.id.comment_view_conten);
            userImage = itemView.findViewById(R.id.comment_view_userimage);
        }
    }
}
