package com.example.oldstocktrade;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyboardShortcutGroup;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Adapter.MessageAdapter;
import com.example.oldstocktrade.Model.Chat;
import com.example.oldstocktrade.Model.Conversation;
import com.example.oldstocktrade.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private CircleImageView profile_image;
    private TextView txt_username;
    private RecyclerView recyclerView;

    private FirebaseUser fuser;
    private DatabaseReference reference;
    private ImageButton btn_send;
    private ImageButton btn_img;
    private ImageButton btn_call;
    private EditText txt_msg;
    private String conversation_id = null;
    private DatabaseReference conversation_reference = null;
    private MessageAdapter messageAdapter;
    private List<Chat> mChats;
    private String imageURL;
    private ImageView img_on;
    private ImageView img_off;
    private StorageTask uploadTask;
    private Uri fileUri;
    private String userid = "";
    private ProgressDialog dialog;
    private User user;
    ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        // Set up
        img_on = findViewById(R.id.img_on);
        img_off = findViewById(R.id.img_off);
        profile_image = findViewById(R.id.profile_image);
        txt_username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        btn_img = findViewById(R.id.btn_img);
        btn_call = findViewById(R.id.btn_call);
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
                //reference.removeEventListener(valueEventListener);
                finish();
                //startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });


        // Load receiver's profile image
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");


        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                txt_username.setText(user.getUsername().toString());
                imageURL = user.getImageURL();
                if (imageURL.equals("default"))
                    profile_image.setImageResource(R.mipmap.ic_launcher_round);
                else Glide.with(MessageActivity.this).load(imageURL).into(profile_image);
                if (user.getStatus().equals("online")){
                    img_on.setVisibility(View.VISIBLE);
                    img_off.setVisibility(View.GONE);
                }else {
                    img_off.setVisibility(View.VISIBLE);
                    img_on.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        //reference.addValueEventListener(valueEventListener);

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
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i.createChooser(i,"Select Image"),438);
            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:"+user.getPhone()));
                startActivity(i);
            }
        });
        txt_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, SellerFeedActivity.class).addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                intent.putExtra("userid",user.getId());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 438 && resultCode ==RESULT_OK && data != null && data.getData() != null){
            dialog = new ProgressDialog(this);
            dialog.setMessage("Sending image...");
            dialog.show();
            fileUri = data.getData();
            StorageReference reference_storage = FirebaseStorage.getInstance().getReference().child("Images");
            if (conversation_reference == null) {
                createConversation(fuser.getUid(),userid);
            }
            DatabaseReference messagePushRef = conversation_reference.child("Chats").push();
            String messagePushID = messagePushRef.getKey();

            final StorageReference filePath = reference_storage.child(messagePushID+".jpg");
            uploadTask = filePath.putFile(fileUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        dialog.dismiss();
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUrl = task.getResult();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("sender",fuser.getUid());
                        hashMap.put("receiver",userid);
                        hashMap.put("message",downloadUrl.toString());
                        hashMap.put("type","image");
                        hashMap.put("time",System.currentTimeMillis());
                        messagePushRef.setValue(hashMap);

                        reference.child("Conversations").child(conversation_reference.getKey()).child("recent_msg").setValue(hashMap);
                        ref.child(conversation_reference.getKey()).child("recent_msg").setValue(hashMap);
                        dialog.dismiss();
                    }
                    else dialog.dismiss();
                }
            });

        }
    }
    DatabaseReference ref;
    void findConversationId(String user1, String user2) {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Conversations");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    Conversation conversation = data.getValue(Conversation.class);
                    if ((conversation.getUser1().equals(user1) && conversation.getUser2().equals(user2)) || conversation.getUser1().equals(user2) && conversation.getUser2().equals(user1)){
                        conversation_id = data.getKey();
                        break;
                    }

                }
                if (conversation_id != null && conversation_reference == null){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Conversations").child(conversation_id);
                    conversation_reference = reference.getRef();
                    loadMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    void sendMessage(String sender,String receiver, String msg){
        if (conversation_reference == null) {
            createConversation(sender,receiver);
        }
        DatabaseReference ref_chats = conversation_reference.child("Chats").push();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",msg);
        hashMap.put("type","text");
        hashMap.put("id",ref_chats.getKey());
        hashMap.put("time", System.currentTimeMillis());
        ref_chats.setValue(hashMap);

        reference.child("Conversations").child(conversation_reference.getKey()).child("recent_msg").setValue(hashMap);
        ref.child(conversation_reference.getKey()).child("recent_msg").setValue(hashMap);
    }
    void createConversation(String sender, String receiver){
        DatabaseReference ref_con = FirebaseDatabase.getInstance().getReference("Conversations");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user1", sender);
        hashMap.put("user2", receiver);
        conversation_reference = ref_con.push();
        conversation_reference.setValue(hashMap);

        reference.child("Conversations").child(conversation_reference.getKey()).setValue(hashMap);
        ref.child(conversation_reference.getKey()).setValue(hashMap);
        loadMessage();
    }
    void loadMessage() {
        if (conversation_reference != null){
            DatabaseReference reference = conversation_reference.child("Chats");
            mChats = new ArrayList<>();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mChats.clear();
                    for(DataSnapshot data : snapshot.getChildren()){
                        Chat chat = data.getValue(Chat.class);
                        mChats.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChats, imageURL, conversation_reference);
                    recyclerView.setAdapter(messageAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        reference.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(valueEventListener);
    }
}