package com.example.oldstocktrade.Fragment;

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
import com.example.oldstocktrade.R;
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
    private List<Boolean> mIsSeen;
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
            mIsSeen = new ArrayList<>();

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            recyclerView = view.findViewById(R.id.recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            reference.child(firebaseUser.getUid()).child("Conversations").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mUsers.clear();
                    min_time.clear();
                    last_message.clear();
                    mIsSeen.clear();
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
                                    mIsSeen.clear();
                                }else insert(chat,user);

                                contactAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                    contactAdapter = new ContactAdapter(getContext(), mUsers, min_time, last_message, mIsSeen);
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
    void insert( Chat chat, User user){
        String message= "";
        long time = chat.getTime();
        Boolean isSeen = chat.getSeen();
        if (chat.getSender().equals(firebaseUser.getUid()))
            isSeen = true;
        else if (isSeen == null)
            isSeen = false;

        if (chat.getType().equals("text")){
            message = chat.getMessage().split("\n")[0];
            if (message.length() > 20)
                message = message.substring(0,20) + "...";
        }
        else if(chat.getType().equals("image")){
            if (chat.getSender().equals(firebaseUser.getUid()))
                message = "You sent an image";
            else message= "You received an image";
        }else if (chat.getType().equals("location")){
            if (chat.getSender().equals(firebaseUser.getUid()))
                message = "You shared your current location";
            else message= "You received a location";
        }
        if (mUsers.size() == 0){
            mUsers.add(user);
            min_time.add(time);
            last_message.add(message);
            mIsSeen.add(isSeen);
            return;
        }
        for(int i=0;i<mUsers.size();i++){
            if (mUsers.get(i).getId().equals(user.getId())){
                mUsers.remove(i);
                min_time.remove(i);
                last_message.remove(i);
                mIsSeen.remove(i);
                break;
            }
        }
        for (int i=0;i<mUsers.size();i++){
            if (min_time.get(i) < time){
                mUsers.add(i,user);
                min_time.add(i,time);
                last_message.add(i,message);
                mIsSeen.add(i,isSeen);
                return;
            }
        }
        mUsers.add(user);
        min_time.add(time);
        last_message.add(message);
        mIsSeen.add(isSeen);

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
