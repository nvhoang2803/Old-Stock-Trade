package com.example.oldstocktrade.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Model.Comment;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    Activity curActivity;
    User tmp;
    public ListViewAdapter(ArrayList<Product> productArrayList, Activity curAcc) {
        this.curActivity = curAcc;
        this.productArrayList = productArrayList;
    }

    ArrayList<Product> productArrayList;
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();

    @Override
    public int getCount() {
        return productArrayList.size();
    }

    @Override
    public Product getItem(int position) {
        return productArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View productView;
        TextView userName;
        ImageView userImage;
        ImageView productImage;

        TextView productAddress;
        TextView productDistance;
        TextView productDetail;

        if (convertView == null){
            productView = View.inflate(parent.getContext(), R.layout.product_view ,null);
        }else{
            productView = convertView;
        }

        userName = productView.findViewById(R.id.userName);
        productAddress = productView.findViewById(R.id.productAddress);
        productDetail = productView.findViewById(R.id.productDetail);
        productDistance = productView.findViewById(R.id.productDistance);
        userImage = productView.findViewById(R.id.userImage);
        productImage = productView.findViewById(R.id.productImage);

        mReference.child("Users").orderByChild("id").equalTo( productArrayList.get(position).getSeller())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    User tmp = ds.getValue(User.class);
                    Glide.with(userImage).load(tmp.getImageURL())
                            .into(userImage);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        long time = System.currentTimeMillis() - productArrayList.get(position).getTimestamp();
        String timeD = "";
        time = time /1000;
        if (time % (60* 60 * 24) > 0){
            timeD = time / (60* 60 * 24) + " d";
        }else if (time % (60* 60 * 24) > 0){
            timeD = time / (60* 60) + " h";
        }else{
            timeD = time / (60) + " m";
        }

        ImageView productComment;
        productComment = productView.findViewById(R.id.product_comment);
        productComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(curActivity,R.style.BottomSheetDialogdTheme);
                View bottomShettView = LayoutInflater.from(curActivity).inflate(R.layout.layout_bottomesheet_comment,
                        curActivity.findViewById(R.id.bottomshett_comment_container));

                bottomShettView.findViewById(R.id.bottomsheet_commend_send).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                ImageView tIm = bottomShettView.findViewById(R.id.current_user_image);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                mReference.child("Users").orderByChild("id").equalTo(user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    tmp = ds.getValue(User.class);
                                    Glide.with(tIm).load(tmp.getImageURL()).into(tIm);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                Button sendComment = bottomShettView.findViewById(R.id.bottomsheet_commend_send);
                sendComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mKey = mReference.child("Comments").push().getKey();
                        Comment newCommet = new Comment(
                               ((TextView) bottomShettView.findViewById(R.id.current_user_comment)).getText().toString(),
                                tmp.getId(),
                                productArrayList.get(position).getProID(),
                                System.currentTimeMillis(),
                                tmp.getId());
                        mReference.child("Comments").child(mKey).setValue(newCommet);
                        hanldeComment(bottomShettView);
                    }
                });
                hanldeComment(bottomShettView);
                bottomSheetDialog.setContentView(bottomShettView);
                bottomSheetDialog.show();
            }
        });

        productAddress.setText(productArrayList.get(position).getAddress() + "-" + timeD);
        productDetail.setText(productArrayList.get(position).getDescription());
        productDistance.setText("5km");
        userName.setText(productArrayList.get(position).getName());
        Glide.with(productImage).load(productArrayList.get(position).getImageURL())
                .into(productImage);
        return productView;
    }

    public void hanldeComment(View bottomShettView){
        mReference.child("Comments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Comment> arrComment = new ArrayList<>();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Comment tmp = ds.getValue(Comment.class);
                    arrComment.add(tmp);
                }

                ListViewCommentAdapter listViewCommentAdapter = new ListViewCommentAdapter(arrComment);
                ListView commentView = bottomShettView.findViewById(R.id.bottomsheet_coment);
                commentView.setAdapter(listViewCommentAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void filter(int price,int distance, int rating){


    }
}
