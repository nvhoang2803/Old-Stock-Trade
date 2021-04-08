package com.example.oldstocktrade;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.oldstocktrade.Adapter.BuyingItemAdapter;
import com.example.oldstocktrade.Adapter.ContactAdapter;
import com.example.oldstocktrade.Model.BuyingItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BuyingFragment extends Fragment {
    private RecyclerView recyclerView;
    private FirebaseUser firebaseUser;
    private List<BuyingItem> mItems;
    private BuyingItemAdapter buyingItemAdapter;
    public BuyingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItems = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buying, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref_buying = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Buying");
        ref_buying.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mItems.clear();
                for(DataSnapshot data : snapshot.getChildren()){
                    BuyingItem item = data.getValue(BuyingItem.class);
                    mItems.add(item);
                }
                 buyingItemAdapter = new BuyingItemAdapter(getContext(), mItems);
                recyclerView.setAdapter(buyingItemAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}