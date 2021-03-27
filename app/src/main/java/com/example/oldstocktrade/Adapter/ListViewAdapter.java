package com.example.oldstocktrade.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.CustomComparator;
import com.example.oldstocktrade.MainActivity;
import com.example.oldstocktrade.Model.Comment;
import com.example.oldstocktrade.Model.MyProduct;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.R;
import com.example.oldstocktrade.utils.BasicFunctions;
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
import java.util.Collections;
import java.util.Comparator;

public class ListViewAdapter extends BaseAdapter {

    Activity curActivity;
    User tmp;
    ArrayList<String> userProductlike;
    public ListViewAdapter(ArrayList<Product> productArrayList, Activity curAcc,User a,ArrayList<String> a1) {
        this.userProductlike = a1;
        this.curActivity = curAcc;
        this.productArrayList = productArrayList;
        this.tmp = a;
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

        long time = System.currentTimeMillis() - productArrayList.get(position).getTimestamp();
        String timeD = "";
        String priceD = "";
        time = time /1000;
        if (time / (60* 60 * 24) > 0){
            timeD = time / (60* 60 * 24) + " days";
        }else if (time / (60* 60 * 24) > 0){
            timeD = time / (60* 60) + " hours";
        }else{
            timeD = time / (60) + " mins";
        }
        double price = productArrayList.get(position).getPrice();

        if (price / (1000 * 1000) > 0){
            priceD = (int) price / (1000 * 1000) + " Bilion";
        }else if (price / (1000) > 0){
            priceD = (int)price / (1000 ) + " Milion";
        }else{
            priceD =(int) price + " K";
        }

        mReference.child("Users").orderByChild("id").equalTo(productArrayList.get(position).getSeller())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                                Glide.with(userImage).load(appleSnapshot.getValue(User.class).getImageURL())
                                .into(userImage);
                            }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //

        ImageView productComment;
        productComment = productView.findViewById(R.id.product_comment);
        //Handle button Like
        ImageView productLike;
        productLike = productView.findViewById(R.id.product_like);
        if (userProductlike.contains(productArrayList.get(position).getProID())){

            productLike.setImageResource(R.drawable.ic_like__1_);
        }
        productLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(productArrayList.get(position).getProID());
                if(productLike.getDrawable().getConstantState()==
                        productView.getResources().getDrawable(R.drawable.ic_heart).getConstantState()){
                    String mKey = mReference.child("MyProducts").push().getKey();
                    MyProduct Mpro = new MyProduct(productArrayList.get(position).getProID(), tmp.getId());
                    mReference.child("MyProducts").child(mKey).setValue(Mpro);
                    productLike.setImageResource(R.drawable.ic_like__1_);
                }else{
                    mReference.child("MyProducts").orderByChild("userID").
                            equalTo(tmp.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot appleSnapshot: snapshot.getChildren()) {

                                MyProduct yP = appleSnapshot.getValue(MyProduct.class);
                                if (yP.getProID().equals(productArrayList.get(position).getProID())){
                                    appleSnapshot.getRef().removeValue();
                                    productLike.setImageResource(R.drawable.ic_heart);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
//                mReference.child("MyProducts").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()){
//                            for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
//                                appleSnapshot.getRef().removeValue();
//                            }
//                            productLike.setImageResource(R.drawable.ic_heart);
//                        }else{
//                            String mKey = mReference.child("MyProducts").push().getKey();
//                            MyProduct Mpro = new MyProduct(productArrayList.get(position).getProID(),
//                                    tmp.getId());
//                            mReference.child("MyProducts").child(mKey).setValue(Mpro);
//                            productLike.setImageResource(R.drawable.ic_like__1_);
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
            }
        });

        //Handle Comment
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
                    @RequiresApi(api = Build.VERSION_CODES.M)
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
                        ((TextView) bottomShettView.findViewById(R.id.current_user_comment)).setText("");

                        hanldeComment(bottomShettView);
                    }
                });
                hanldeComment(bottomShettView);
                bottomSheetDialog.setContentView(bottomShettView);
                bottomSheetDialog.show();
            }
        });

        //
        productAddress.setText(productArrayList.get(position).getAddress());
        //
        ((TextView)productView.findViewById(R.id.productTime)).setText(timeD + " - " + priceD);
        //
        productDetail.setText(productArrayList.get(position).getDescription());
        //Caculate distance from currenLocation to product location
        double dis = BasicFunctions.calDistance(((MainActivity) curActivity).longitude,
                ((MainActivity) curActivity).latitude,
                productArrayList.get(position).getLongtitude(),
                productArrayList.get(position).getLatitude());

        dis = Math.floor(dis);
        productDistance.setText((int) dis + "km");

        userName.setText(productArrayList.get(position).getName());
        Glide.with(productImage).load(productArrayList.get(position).getImageURL())
                .into(productImage);
        return productView;
    }

    public void hanldeComment(View bottomShettView){
        mReference.child("Comments").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Comment> arrComment = new ArrayList<>();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Comment tmp = ds.getValue(Comment.class);
                    arrComment.add(tmp);
                }
                arrComment.sort(Comparator.comparing(Comment::getTimestamp));

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
