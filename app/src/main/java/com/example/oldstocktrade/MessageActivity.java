package com.example.oldstocktrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Adapter.MessageAdapter;
import com.example.oldstocktrade.Model.Chat;
import com.example.oldstocktrade.Model.Conversation;
import com.example.oldstocktrade.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView txt_username;
    RecyclerView recyclerView;

    FirebaseUser fuser;
    DatabaseReference reference;
    ImageButton btn_send;
    EditText txt_msg;
    String conversation_id = null;
    DatabaseReference conversation_reference = null;
    MessageAdapter messageAdapter;
    List<Chat> mChats;
    private String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        // Set up
        profile_image = findViewById(R.id.profile_image);
        txt_username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        txt_msg = findViewById(R.id.txt_msg);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Create toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Load receiver's profile image
        Intent intent = getIntent();
        String userid = intent.getStringExtra("userid");


        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                txt_username.setText(user.getUsername().toString());
                imageURL = user.getImageURL();
                if (imageURL.equals("default"))
                    profile_image.setImageResource(R.mipmap.ic_launcher_round);
                else Glide.with(MessageActivity.this).load(imageURL).into(profile_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Find conversation between 2 users
        findConversationId(fuser.getUid(),userid);

        // Send message
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = txt_msg.getText().toString();
                if (!msg.equals("")){
                    sendMessage(fuser.getUid(),userid,msg);
                    txt_msg.setText("");
                }
            }
        });



    }

    void findConversationId(String user1, String user2) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Conversations");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    Conversation conversation = data.getValue(Conversation.class);
                    if ((conversation.getUser1().equals(user1) && conversation.getUser2().equals(user2)) || conversation.getUser1().equals(user2) && conversation.getUser2().equals(user1)){
                        conversation_id = data.getKey();
                        conversation_reference = data.getRef();
                        break;
                    }
                }
                if (conversation_reference == null){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("user1", user1);
                    hashMap.put("user2", user2);
                    conversation_reference = reference.push();
                    conversation_reference.setValue(hashMap);
                }
                loadMessage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void sendMessage(String sender,String receiver, String msg){
        DatabaseReference reference = conversation_reference;
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",msg);
        reference.child("Chats").push().setValue(hashMap);
    }

    void loadMessage() {
        DatabaseReference reference = conversation_reference.child("Chats");
        mChats = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    Chat chat = data.getValue(Chat.class);
                    mChats.add(chat);
                }
                messageAdapter = new MessageAdapter(MessageActivity.this, mChats, imageURL );
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}