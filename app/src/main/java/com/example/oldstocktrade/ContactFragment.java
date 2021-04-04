package com.example.oldstocktrade;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oldstocktrade.Adapter.ContactAdapter;
import com.example.oldstocktrade.Model.Chat;
import com.example.oldstocktrade.Model.Contact;
import com.example.oldstocktrade.Model.Conversation;
import com.example.oldstocktrade.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment {
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<User> mUsers;
    private List<Long> min_time;
    private List<String> last_message;
    DatabaseReference reference;
    FirebaseUser firebaseUser;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_contact, container, false);
            reference = FirebaseDatabase.getInstance("https://old-stock-trade-default-rtdb.firebaseio.com/").getReference("Users");
            //mMessages = new ArrayList<>();
            mUsers = new ArrayList<>();
            min_time = new ArrayList<>();
            last_message = new ArrayList<>();

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            recyclerView = view.findViewById(R.id.recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            reference.child(firebaseUser.getUid()).child("Conversations").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mUsers.clear();
                    min_time.clear();
                    last_message.clear();
                    for(DataSnapshot data: snapshot.getChildren()){
                        Conversation conversation = data.getValue(Conversation.class);
                        String userid = "";
                        if (conversation.getUser1().equals(firebaseUser.getUid()))
                            userid = conversation.getUser2();
                        else userid = conversation.getUser1();
                        reference.child(userid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                //mUsers.add(user);
                                Chat chat = data.child("recent_msg").getValue(Chat.class);
                                if (chat == null){
                                    mUsers.clear();
                                    min_time.clear();
                                    last_message.clear();
                                }else insert(chat.getTime(),chat,user);

                                contactAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                    contactAdapter = new ContactAdapter(getContext(), mUsers, min_time, last_message);
                    recyclerView.setAdapter(contactAdapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    void insert(long time, Chat chat, User user){
        String message= "";
        if (chat.getType().equals("text")){
            message = chat.getMessage().split("\n")[0];
            if (message.length() > 20)
                message = message.substring(0,20) + "...";
        }
        else if(chat.getType().equals("image")){
            if (chat.getSender().equals(firebaseUser.getUid()))
                message = "You sent an image";
            else message= "You received an image";
        }
        if (mUsers.size() == 0){
            mUsers.add(user);
            min_time.add(time);
            last_message.add(message);
            return;
        }
        for(int i=0;i<mUsers.size();i++){
            if (mUsers.get(i).getId().equals(user.getId())){
                mUsers.remove(i);
                min_time.remove(i);
                last_message.remove(i);
                break;
            }
        }
        for (int i=0;i<mUsers.size();i++){
            if (min_time.get(i) < time){
                mUsers.add(i,user);
                min_time.add(i,time);
                last_message.add(i,message);
                return;
            }
        }
        mUsers.add(user);
        min_time.add(time);
        last_message.add(message);

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
