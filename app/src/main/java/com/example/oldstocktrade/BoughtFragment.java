package com.example.oldstocktrade;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.oldstocktrade.Adapter.HistoryAdapter;
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

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class BoughtFragment extends Fragment {
    View v;
    private RecyclerView myrecycleview;
    private List<Product> lstProduct;
    private DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser fuser;
    private String userID;
    private LinearLayout oopslayout;
    private ImageView oops;
    private Activity curActivity;
    BoughtFragment(Activity curActivity){
        this.curActivity = curActivity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        v = inflater.inflate(R.layout.fragment_bought, container, false);
        lstProduct = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userID = fuser.getUid();
        oopslayout = (LinearLayout) v.findViewById(R.id.oops_layout_bought);
        SoldFragment.createNoStockLayout(oopslayout);
        mReference.child("Products").orderByChild("Buyer").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Product tmp = ds.getValue(Product.class);
                    if (tmp.getStatus() == 0 && tmp.isVisibleToBuyer()) {
                        lstProduct.add(tmp);
                    }
                }
                if (lstProduct.size() == 0){
                    SoldFragment.createNoStockLayout(oopslayout);
                }
                else {
                    SoldFragment.removeNoStockLayout(oopslayout);
                    myrecycleview = (RecyclerView) v.findViewById(R.id.history_recyclerview);
                    HistoryAdapter recyclerAdapter = new HistoryAdapter(getContext(), curActivity, lstProduct,"bought");
                    myrecycleview.setLayoutManager(new GridLayoutManager(getActivity(),2));
                    myrecycleview.setAdapter(recyclerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return v;
    }


}