package com.example.oldstocktrade.Adapter;


import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.MainActivity;
import com.example.oldstocktrade.Model.Comment;
import com.example.oldstocktrade.Model.Wishlist;
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

import java.util.ArrayList;
import java.util.Comparator;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ViewHolder> {

    Activity curActivity;
    User tmp;
    ArrayList<String> userProductlike;
    ArrayList<Product> productArrayList;
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<Double> lonlat;

    public ListViewAdapter(ArrayList<Product> productArrayList, Activity curAcc, User a, ArrayList<String> a1,ArrayList<Double> lonlat) {
        this.userProductlike = a1;
        this.curActivity = curAcc;
        this.productArrayList = productArrayList;
        this.tmp = a;
        this.lonlat = lonlat;
    }

    @NonNull
    @Override
    public ListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(curActivity).inflate(R.layout.product_view,parent,false);
        return new ListViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewAdapter.ViewHolder holder, int position) {
        //Get user image
        mReference.child("Users").orderByChild("id").equalTo(productArrayList.get(position).getSeller())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                            Glide.with(holder.userImage).load(appleSnapshot.getValue(User.class).getImageURL())
                                    .into(holder.userImage);
                            holder.productSellerName.setText(appleSnapshot.getValue(User.class).getUsername());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        //Set user add to favorite list
        if (userProductlike.contains(productArrayList.get(position).getProID())){
            holder.productLike.setImageResource(R.drawable.ic_like__1_);
        }

    //
        long time = System.currentTimeMillis() - productArrayList.get(position).getTimestamp();
        String timeD = "";
        String priceD = "";
        time = time /1000;
        if (time / (60* 60 * 24) > 0){
            timeD = (int) ( time / (60* 60 * 24)) + " days";
        }else if (time / (60 * 60 ) > 0){
            timeD =(int) ( time / (60* 60)) + " hours";
        }else{
            timeD = (int) (time / (60)) + " mins";
        }
        double price = productArrayList.get(position).getPrice();
        priceD = ChangeMoneyToString((int) price);

        //
        mReference.child("Users").orderByChild("id").equalTo(productArrayList.get(position).getSeller())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                            Glide.with(holder.userImage).load(appleSnapshot.getValue(User.class).getImageURL())
                                    .into(holder.userImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        holder.productComment.setOnClickListener(new View.OnClickListener() {
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
                        hanldeComment(bottomShettView,productArrayList.get(position).getProID());
                    }
                });
                hanldeComment(bottomShettView,productArrayList.get(position).getProID());
                bottomSheetDialog.setContentView(bottomShettView);
                bottomSheetDialog.show();
            }
        });

        holder.productAddress.setText(productArrayList.get(position).getAddress());
        //
        holder.productTime.setText(timeD + " - " + priceD + " VND");
        //
        holder.productDetail.setText(productArrayList.get(position).getDescription());
        //Caculate distance from currenLocation to product location
        double dis = 0;

        if (lonlat != null){
            dis = BasicFunctions.calDistance(lonlat.get(0), lonlat.get(1),
                    productArrayList.get(position).getLongitude(),
                    productArrayList.get(position).getLatitude());
        }else{
             dis = BasicFunctions.calDistance(((MainActivity) curActivity).longitude,
                    ((MainActivity) curActivity).latitude,
                    productArrayList.get(position).getLongitude(),
                    productArrayList.get(position).getLatitude());
        }

        dis = Math.floor(dis);


        holder.productDistance.setText((int) dis + "km");
        holder.userName.setText(productArrayList.get(position).getName());
        //Handle ImageSlider
        String[] arrImage = productArrayList.get(position).getImageURL().toArray(new String[0]);
        ImageSlider imgSliderAdapter = new ImageSlider(curActivity,arrImage);
        holder.productImage.setAdapter(imgSliderAdapter);
        //Handle ImageSlider Dot

        if (arrImage.length > 1){
            holder.productImageDotSlider.removeAllViews();
            ImageView[] dots = new ImageView[arrImage.length];
            for (int i=0;i< arrImage.length;i++){
                dots[i] = new ImageView(curActivity);
                dots[i].setImageResource(R.drawable.ic_circle_nonactive);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8,0,8,0);

                holder.productImageDotSlider.addView(dots[i],params);
            }
            dots[0].setImageResource(R.drawable.ic_circle);
            holder.productImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }
                @Override
                public void onPageSelected(int position) {
                    for (int i=0;i< arrImage.length;i++){
                        dots[i].setImageResource(R.drawable.ic_circle_nonactive);
                    }
                    dots[position].setImageResource(R.drawable.ic_circle);
                }
                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }


        //Hanlde product like behavior
        holder.productLike.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(holder.productLike.getDrawable().getConstantState()==
                        curActivity.getDrawable(R.drawable.ic_heart).getConstantState()){
                    String mKey = mReference.child("Wishlist").push().getKey();
                    Wishlist Mpro = new Wishlist(productArrayList.get(position).getProID(), tmp.getId());
                    mReference.child("Wishlist").child(mKey).setValue(Mpro);
                    holder.productLike.setImageResource(R.drawable.ic_like__1_);
                }else{
                    mReference.child("Wishlist").orderByChild("userID").
                            equalTo(tmp.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                                Wishlist yP = appleSnapshot.getValue(Wishlist.class);
                                if (yP.getProID().equals(productArrayList.get(position).getProID())){
                                    appleSnapshot.getRef().removeValue();
                                    holder.productLike.setImageResource(R.drawable.ic_heart);
                                    break;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });

        if (productArrayList.get(position).getStatus() != 1){
            holder.productStatus.setVisibility(View.VISIBLE);
            holder.productStatus.bringToFront();
        }

    }

    public void hanldeComment(View bottomShettView,String proID){
        mReference.child("Comments").orderByChild("proID").equalTo(proID).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Comment> arrComment = new ArrayList<>();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Comment tmp = ds.getValue(Comment.class);
                    arrComment.add(tmp);
                }
                arrComment.sort(Comparator.comparing(Comment::getTimestamp));

                ListViewCommentAdapter listViewCommentAdapter = new ListViewCommentAdapter(arrComment,curActivity);

                RecyclerView commentView = bottomShettView.findViewById(R.id.bottomsheet_coment);
                commentView.setAdapter(listViewCommentAdapter);
                commentView.setLayoutManager(new LinearLayoutManager(curActivity));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView userImage;
        ViewPager productImage;
        TextView productAddress;
        TextView productDistance;
        TextView productDetail;
        TextView productSellerName;
        ImageView productComment;
        ImageView productLike;
        TextView productTime;
        LinearLayout productImageDotSlider;
        ImageView productStatus;
        LinearLayout productRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            productAddress = itemView.findViewById(R.id.productAddress);
            productDetail = itemView.findViewById(R.id.productDetail);
            productDistance = itemView.findViewById(R.id.productDistance);
            userImage = itemView.findViewById(R.id.userImage);
            productImage = itemView.findViewById(R.id.productImage);
            productComment = itemView.findViewById(R.id.product_comment);
            productLike = itemView.findViewById(R.id.product_like);
            productTime = itemView.findViewById(R.id.productTime);
            productImageDotSlider = itemView.findViewById(R.id.productImageDotSlider);
            productStatus = itemView.findViewById(R.id.product_status);
            productSellerName = itemView.findViewById(R.id.productSellerName);
            productRating = itemView.findViewById(R.id.productRating);
        }
    }


    public String ChangeMoneyToString(int price){
        int cur;
        String priceD = "";
        String result = "";
        priceD = String.valueOf(price);
        cur = priceD.length() - 3;
        for (int i =0 ; i< ((int)(priceD.length() / 3));i++ ){
            result = "." + priceD.substring(cur,cur +3)+ result;
            cur = cur -3;
        }
        cur = cur + 3;
        result = priceD.substring(0, cur) + result;
        return result;
    }
}


