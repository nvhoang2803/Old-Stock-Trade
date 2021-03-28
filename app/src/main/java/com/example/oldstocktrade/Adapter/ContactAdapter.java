package com.example.oldstocktrade.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.MessageActivity;
import com.example.oldstocktrade.Model.Chat;
import com.example.oldstocktrade.Model.Conversation;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    Context context;
    List<User> mUsers;
    public ContactAdapter(@NonNull Context context, List<User> mUsers) {
        this.context = context;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_contact,parent,false);
        return new ContactAdapter.ViewHolder(view);
    }

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

        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Conversations");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference conversation_reference = null;
                for(DataSnapshot data : snapshot.getChildren()){
                    Conversation conversation = data.getValue(Conversation.class);
                    if ((conversation.getUser1().equals(fuser.getUid()) && conversation.getUser2().equals(user.getId())) || conversation.getUser1().equals(user.getId()) && conversation.getUser2().equals(fuser.getUid())){
                        conversation_reference = data.getRef();
                        break;
                    }
                }

                if (conversation_reference != null){
                    conversation_reference.child("Chats").addValueEventListener(new ValueEventListener() {
                        String last_message = "";
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Chat chat = null;
                            for(DataSnapshot data : snapshot.getChildren()){
                                chat = data.getValue(Chat.class);
                                last_message = chat.getMessage();
                            }
                            if (chat != null){
                                if (chat.getType().equals("text")){
                                    String recentMsg = last_message.split("\n")[0];
                                    if (recentMsg.length() > 20)
                                        recentMsg = recentMsg.substring(0,20) + "...";
                                    holder.last_msg.setText(recentMsg);
                                }
                                else if(chat.getType().equals("image")){
                                    if (chat.getSender().equals(fuser.getUid()))
                                        holder.last_msg.setText("You sent an image");
                                    else holder.last_msg.setText("You received an image");
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else holder.last_msg.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.ImgID);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }
}
