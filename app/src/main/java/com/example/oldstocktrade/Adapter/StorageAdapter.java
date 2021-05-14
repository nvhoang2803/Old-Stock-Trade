package com.example.oldstocktrade.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Activity.EditActivity;
import com.example.oldstocktrade.Activity.MainActivity;
import com.example.oldstocktrade.Activity.MessageActivity;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Model.WishListItem;
import com.example.oldstocktrade.Activity.ParticularPageActivity;
import com.example.oldstocktrade.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class StorageAdapter extends RecyclerView.Adapter<StorageAdapter.MyViewHolder> {
    Context mContext;
    List<Product> mData;
    String tabName;
    RecyclerView mRecyclerView;
    private ProgressDialog progressDialog;
    private Activity curActivity;
    public StorageAdapter(Context mContext,Activity curActivity, List<Product> mData, String tabName) {
        this.mContext = mContext;
        this.mData = mData;
        this.tabName = tabName;
        this.curActivity = curActivity;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public StorageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.storage_item,parent,false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder  extends RecyclerView.ViewHolder{
        private ImageView proImage;
        private TextView proName;
        private TextView proDate;
        private TextView proPrice;
        private ImageView btnSetting;
        private TextView proBlock;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            proImage = (ImageView) itemView.findViewById(R.id.proImage);
            proName = (TextView) itemView.findViewById(R.id.proName);
            proBlock = (TextView) itemView.findViewById(R.id.blocked);
            proDate = (TextView) itemView.findViewById(R.id.proTransactedDate);
            proPrice = (TextView) itemView.findViewById(R.id.proPrice);
            btnSetting = (ImageView) itemView.findViewById(R.id.btn_setting);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull StorageAdapter.MyViewHolder holder, int position) {
        if (mData.get(position) == null){
            return;
        }
        if (!mData.get(position).isEnable()){
            holder.proBlock.setText("BLOCKED");

        }
        holder.proName.setText(mData.get(position).getName());
        holder.proPrice.setText(Long.toString(Math.round(mData.get(position).getPrice())));
        Date date = new Date(mData.get(position).getTimestamp());
        holder.proDate.setText(DateFormat.getDateInstance().format(date));
        Glide.with(holder.proImage).load(mData.get(position).getImageURL().get(0))
                .into(holder.proImage);
        holder.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(mContext, v);
                    MenuInflater inflater = popup.getMenuInflater();
                    if (tabName == "selling")
                        inflater.inflate(R.menu.selling_menu, popup.getMenu());
                    else
                        inflater.inflate(R.menu.wishlist_menu,popup.getMenu());

                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.edit_item){
                                // edit selling item
                                editProduct(position);
                            } else if (item.getItemId() == R.id.remove_item){
                                // remove item
                                removeFromSelling(position);
                            } else if (item.getItemId() == R.id.removefromwishlist_item){
                                // remove from wishlist
                                removeFromWishList(position);
                            }else {
                                sendChat(position);
                            }
                            return false;
                        }
                    });

            }
        }
        );
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id= mData.get(position).getProID();
                String userID= mData.get(position).getSeller();
                Intent partIntent= new Intent(v.getContext(), ParticularPageActivity.class);
                partIntent.putExtra("id", id);
                partIntent.putExtra("userID", userID);
                partIntent.putExtra("sizeImageURL", mData.get(position).getImageURL().size());
                partIntent.putExtra("sumReport", mData.get(position).getReport());
                partIntent.putExtra("longitude", ((MainActivity) curActivity).longitude);
                partIntent.putExtra("latitude",((MainActivity) curActivity).latitude);
                partIntent.putExtra("address",((MainActivity) curActivity).address);
                Log.d("PackInHomeView", "onClick: ");
                v.getContext().startActivity(partIntent);
            }
        });
    }
    public void removeFromSelling(int position){
        progressDialog= new ProgressDialog(mContext);
        progressDialog.setMessage("Removing...");
        progressDialog.show();
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query mWishList = FirebaseDatabase.getInstance().getReference("Wishlist").orderByChild("proID").equalTo(mData.get(position).getProID());
        mWishList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot wishlistSnapshot: snapshot.getChildren()) {
                    if(wishlistSnapshot.getValue(WishListItem.class).getUserID().equals(fuser.getUid())){
                        wishlistSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                } else {
                                }
                            }
                        });;
                    };
                }
                DatabaseReference mProduct = FirebaseDatabase.getInstance().getReference("Products").child(mData.get(position).getProID());
                mProduct.removeValue();
                notifyItemRemoved(position);
                notifyItemRangeRemoved(position, mData.size());
                mData.remove(position);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(mRecyclerView != null){
            mRecyclerView.removeViewAt(position);
        }
    }
    public void removeFromWishList(int position){
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query mWishList = FirebaseDatabase.getInstance().getReference("Wishlist").orderByChild("proID").equalTo(mData.get(position).getProID());
        mWishList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot wishlistSnapshot: snapshot.getChildren()) {
                    if(wishlistSnapshot.getValue(WishListItem.class).getUserID().equals(fuser.getUid())){
                        wishlistSnapshot.getRef().removeValue();
                    };
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mData.remove(position);
        mRecyclerView.removeViewAt(position);
        notifyItemRemoved(position);
        notifyItemRangeRemoved(position, mData.size());
    }
    public void sendChat(int position){
        Intent intent = new Intent(mContext, MessageActivity.class).addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.putExtra("userid", mData.get(position).getSeller());
        mContext.startActivity(intent);
    }
    public void editProduct(int position) {
        Intent intent = new Intent(mContext, EditActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putString("Address", mData.get(position).getAddress());
        myBundle.putString("ProID", mData.get(position).getProID());
        myBundle.putString("Buyer", mData.get(position).getBuyer());
        myBundle.putString("Seller", mData.get(position).getSeller());
        myBundle.putString("Description", mData.get(position).getDescription());
        myBundle.putDouble("Latitude", mData.get(position).getLatitude());
        myBundle.putDouble("Longitude", mData.get(position).getLongitude());
        myBundle.putString("Name", mData.get(position).getName());
        myBundle.putDouble("Price", mData.get(position).getPrice());
        myBundle.putInt("Report", mData.get(position).getReport());
        myBundle.putInt("Status", mData.get(position).getStatus());
        myBundle.putLong("Timestamp", mData.get(position).getTimestamp());
        myBundle.putBoolean("VisibleToBuyer", mData.get(position).isVisibleToBuyer());
        myBundle.putBoolean("VisibleToSeller", mData.get(position).isVisibleToSeller());
        myBundle.putStringArrayList("ImageURL",mData.get(position).getImageURL());
        myBundle.putFloat("Rate",mData.get(position).getRate());
        intent.putExtras(myBundle);
        mContext.startActivity(intent);
    }
//    private class MyTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
////            AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
////            inAnimation.setDuration(200);
////            progressDialog= new ProgressDialog(mContext);
////            progressDialog.setMessage("Image Uploading please wait............");
////            progressDialog.show();
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
////            AlphaAnimation outAnimation;
////            outAnimation = new AlphaAnimation(1f, 0f);
////            outAnimation.setDuration(200);
//            progressDialog.dismiss();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            removeFromSelling(position);
//            return null;
//        }
//    }

}
