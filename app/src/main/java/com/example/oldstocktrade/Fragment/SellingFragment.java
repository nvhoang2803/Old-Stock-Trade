package com.example.oldstocktrade.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.oldstocktrade.Activity.PostActivity;
import com.example.oldstocktrade.Adapter.StorageAdapter;
import com.example.oldstocktrade.Activity.MainActivity;
import com.example.oldstocktrade.Model.Product;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class SellingFragment extends Fragment {
    View v;
    private RecyclerView myrecycleview;
    private List<Product> lstProduct;
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser fuser;
    private String userID;
    private LinearLayout oopslayout;
    private Activity curActivity;
    MainActivity main = (MainActivity) getActivity();

    public SellingFragment(Activity curActivity){
        this.curActivity = curActivity;
    }

    public View getView() {
        return v;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        v = inflater.inflate(R.layout.fragment_selling, container, false);
        lstProduct = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userID = fuser.getUid();
        oopslayout = (LinearLayout) v.findViewById(R.id.oops_layout_selling);
        SoldFragment.createNoStockLayout(oopslayout);
        mReference.child("Products").orderByChild("Seller").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Product tmp = ds.getValue(Product.class);
                    if (tmp.getStatus() == 1) {
                        lstProduct.add(tmp);
                    }
                }
                if (lstProduct.size() != 0){
                    SoldFragment.removeNoStockLayout(oopslayout);
                }
                myrecycleview = (RecyclerView) v.findViewById(R.id.storage_recyclerview);
                StorageAdapter recyclerAdapter = new StorageAdapter(getContext(), curActivity,lstProduct,"selling");
                myrecycleview.setLayoutManager(new GridLayoutManager(getActivity(),2));
                myrecycleview.setAdapter(recyclerAdapter);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return v;

    }
    public void editProduct(int position){
        Intent intent = new Intent(getActivity(), PostActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putDouble ("lat",main.latitude);
        myBundle.putDouble ("long",main.longitude);
        intent.putExtras(myBundle);
        startActivity(intent);
    }



}