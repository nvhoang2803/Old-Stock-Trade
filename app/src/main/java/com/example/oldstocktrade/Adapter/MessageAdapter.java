package com.example.oldstocktrade.Adapter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.oldstocktrade.ContactFragment;
import com.example.oldstocktrade.FullscreenActivity;
import com.example.oldstocktrade.MessageActivity;
import com.example.oldstocktrade.Model.Chat;
import com.example.oldstocktrade.R;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<Chat> mChats;
    String imageURL;
    DatabaseReference conversation_ref;

    public MessageAdapter(@NonNull Context context, List<Chat> mChats, String imageURL,DatabaseReference ref) {
        this.context = context;
        this.mChats = mChats;
        this.imageURL = imageURL;
        this.conversation_ref = ref;
    }
    View isClicked = null;
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
            holder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClicked != null)
                        isClicked.setVisibility(View.GONE);
                    holder.btn_delete.setVisibility(View.VISIBLE);
                    isClicked = holder.btn_delete;
                }
            });
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
                    //i.putExtra("conversation_ref", (Parcelable) conversation_ref);
                    context.startActivity(i);
                }
            });
        }else if (chat.getType().equals("location")){
            holder.message.setVisibility(View.VISIBLE);
            holder.message.setText("Current location.");
            holder.image.setVisibility(View.GONE);
            holder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof MessageActivity) {
                        String data[] = chat.getMessage().split(",",2);
                        Double lat = Double.parseDouble(data[0]);
                        Double lon = Double.parseDouble(data[1]);
                        ((MessageActivity) context).showLocation(lat,lon);
                    }
                }
            });
        }
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Delete message")
                        .setMessage("Do you want to remove this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (position == mChats.size() -1){
                                    if (mChats.size() != 1){
                                        DatabaseReference ref = conversation_ref.child("Chats").child(mChats.get(position-1).getId());
                                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Chat chat = snapshot.getValue(Chat.class);
                                                String con_id = conversation_ref.getKey();
                                                FirebaseDatabase.getInstance().getReference("Users").child(chat.getSender()).child("Conversations").child(con_id).child("recent_msg").setValue(chat);
                                                FirebaseDatabase.getInstance().getReference("Users").child(chat.getReceiver()).child("Conversations").child(con_id).child("recent_msg").setValue(chat);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        conversation_ref.child("Chats").child(mChats.get(position).getId()).removeValue();
                                    }
                                    else {
                                        DatabaseReference ref = conversation_ref.child("Chats").child(mChats.get(position).getId());
                                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Chat chat = snapshot.getValue(Chat.class);
                                                String con_id = conversation_ref.getKey();
                                                FirebaseDatabase.getInstance().getReference("Users").child(chat.getSender()).child("Conversations").child(con_id).removeValue();
                                                FirebaseDatabase.getInstance().getReference("Users").child(chat.getReceiver()).child("Conversations").child(con_id).removeValue();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        ref.removeValue();
                                    }
                                }
                                else {
                                    DatabaseReference ref = conversation_ref.child("Chats").child(mChats.get(position).getId());
                                    ref.removeValue();
                                }

                            }
                        })
                        .setNegativeButton("No",null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public CircleImageView profile_image;
        public ImageView image;
        public ImageButton btn_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.message);
            profile_image = itemView.findViewById(R.id.profile_image);
            image = itemView.findViewById(R.id.imageMsg);
            btn_delete = itemView.findViewById(R.id.btn_delete);
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
