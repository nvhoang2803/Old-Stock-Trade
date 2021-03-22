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
import com.example.oldstocktrade.Model.Contact;
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
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_contact, container, false);
            reference = FirebaseDatabase.getInstance("https://old-stock-trade-default-rtdb.firebaseio.com/").getReference("Users");
            mUsers = new ArrayList<>();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            recyclerView = view.findViewById(R.id.recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mUsers.clear();
                    for(DataSnapshot contact : snapshot.getChildren()){
                        User user = contact.getValue(User.class);
                        if (!user.getId().equals(firebaseUser.getUid()))
                            mUsers.add(user);
                    }
                    contactAdapter = new ContactAdapter(getContext(), mUsers);
                    recyclerView.setAdapter(contactAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
