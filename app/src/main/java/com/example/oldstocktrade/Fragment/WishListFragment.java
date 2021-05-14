package com.example.oldstocktrade.Fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.oldstocktrade.Adapter.StorageAdapter;
import com.example.oldstocktrade.Fragment.SoldFragment;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Model.WishListItem;
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
public class  WishListFragment extends Fragment {
    View v;
    private RecyclerView myrecycleview;
    private List<Product> lstProduct;
    private DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser fuser;
    private String userID;
    private ArrayList<String> wishlist;
    private LinearLayout oopslayout;
    private Activity curActivity;
    public WishListFragment(Activity curActivity){
        this.curActivity = curActivity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        v = inflater.inflate(R.layout.fragment_wish_list, container, false);
        lstProduct = new ArrayList<>();
        wishlist = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userID = fuser.getUid();
        oopslayout = (LinearLayout) v.findViewById(R.id.oops_layout_wishlist);
        SoldFragment.createNoStockLayout(oopslayout);
        mReference.child("Wishlist").orderByChild("userID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    WishListItem wl = ds.getValue(WishListItem.class);
                    mReference.child("Products/" + wl.getProID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot childsnapshot) {
                            Product product = childsnapshot.getValue(Product.class);
                            if(product != null && !(product.getSeller().equals(userID))){
                                lstProduct.add(product);
                                SoldFragment.removeNoStockLayout(oopslayout);
                            }
                            myrecycleview = (RecyclerView) v.findViewById(R.id.storage_recyclerview);
                            StorageAdapter recyclerAdapter = new StorageAdapter(getContext(), curActivity, lstProduct,"wishlist");
                            myrecycleview.setLayoutManager(new GridLayoutManager(getActivity(),2));
                            myrecycleview.setAdapter(recyclerAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return v;
    }


}