package com.example.oldstocktrade;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.oldstocktrade.Adapter.RecyclerViewAdapter;
import com.example.oldstocktrade.Model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class BoughtFragment extends Fragment {
    View v;
    private RecyclerView myrecycleview;
    private List<Product> lstProduct;
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser fuser;
    private String userID;
    public BoughtFragment(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        v = inflater.inflate(R.layout.fragment_sold, container, false);
        lstProduct = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userID = fuser.getUid();
        mReference.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Product tmp = ds.getValue(Product.class);
                    if (tmp.getBuyer().equals(userID) && tmp.getStatus() == 0 && tmp.isVisibleToBuyer()) {
                        lstProduct.add(tmp);
                    }
                }
                myrecycleview = (RecyclerView) v.findViewById(R.id.history_recyclerview);
                RecyclerViewAdapter recyclerAdapter = new RecyclerViewAdapter(getContext(),lstProduct);
                myrecycleview.setLayoutManager(new GridLayoutManager(getActivity(),2));
                myrecycleview.setAdapter(recyclerAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return v;
    }


}