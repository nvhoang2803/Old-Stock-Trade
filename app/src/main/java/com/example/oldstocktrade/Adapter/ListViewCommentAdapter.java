package com.example.oldstocktrade.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

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

public class ListViewCommentAdapter extends BaseAdapter {
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<Comment> arr;

    public ListViewCommentAdapter(ArrayList<Comment> arrComment) {
        this.arr = arrComment;
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return arr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView commentUsername;
        TextView commentContent;
        TextView commentTime;
        ImageView userImage;

        View commentView;

        if (convertView == null){
            commentView = View.inflate(parent.getContext(), R.layout.comment_view ,null);
        }else{
            commentView = convertView;
        }

        commentTime = commentView.findViewById(R.id.comment_view_time);
        commentUsername = commentView.findViewById(R.id.comment_view_username);
        commentContent = commentView.findViewById(R.id.comment_view_conten);
        userImage = commentView.findViewById(R.id.comment_view_userimage);

        mReference.child("Users").orderByChild("id").equalTo(arr.get(position).getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            User tmp = ds.getValue(User.class);
                            System.out.println(tmp.getUsername());
                            commentUsername.setText(tmp.getUsername());
                            Glide.with(userImage).load(tmp.getImageURL())
                                    .into(userImage);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        commentContent.setText(arr.get(position).getContent());
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
        commentTime.setText(timeD);

        return commentView;
    }
}
