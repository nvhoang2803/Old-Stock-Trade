package com.example.oldstocktrade.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.oldstocktrade.Activity.LoginActivity;
import com.example.oldstocktrade.Activity.MainActivity;
import com.example.oldstocktrade.Adapter.AdminProductApapter;
import com.example.oldstocktrade.Adapter.ListViewAdapter;
import com.example.oldstocktrade.Adapter.SearchFillAdapter;
import com.example.oldstocktrade.Model.Wishlist;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.Activity.MainActivity;
import com.example.oldstocktrade.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;

public class ManageProductFragment extends Fragment {

    ArrayList<Product> arr;
    SearchView searchView;
    ListView searchFill;
    RecyclerView adminViewProduct;
    ArrayList<String> searcharr;
    //
    SearchFillAdapter searchfillAdapter;

    MainActivity curActivity;

    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    User curUser;

    public ManageProductFragment(MainActivity act){
        curActivity = act;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_admin, container, false);
        arr = new ArrayList<>();

        ((ImageView) view.findViewById(R.id.btnAdminClose)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                //getActivity().finish();
                Log.d("signout", "onClick: Sign out");
            }
        });
        searcharr = new ArrayList<>();
        Log.d("Begin activity", "onCreateView: "+FirebaseAuth.getInstance().getCurrentUser().getUid());
        mReference.child("Users").orderByChild("id")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    curUser = ds.getValue(User.class);
                }
                ((TextView) view.findViewById(R.id.adminName)).setText(curUser.getUsername());
                mReference.child("Products").limitToLast(30).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Product tmp;
                                int i=0;
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    tmp = ds.getValue(Product.class);
                                    arr.add(tmp);
                                    if (i==25) break;
                                }
                                arr.sort(Comparator.comparing(Product::getTimestamp).reversed());
                                AdminProductApapter adminViewProductAdapter = new AdminProductApapter(getActivity(),arr);
                                adminViewProduct = view.findViewById(R.id.admin_productView);
                                adminViewProduct.setAdapter(adminViewProductAdapter);
                                adminViewProduct.setLayoutManager(new LinearLayoutManager(curActivity));

//                                        listViewProduct.setAdapter(listViewProductAdapter);
//                                        listViewProduct.setLayoutManager(new LinearLayoutManager(curActivity));
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.refreshProductView);
//        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                arr.clear();
//                mReference.child("Products").
//                        addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                Product tmp;
//                                int i = 0;
//                                for (DataSnapshot ds: snapshot.getChildren()){
//                                    tmp = ds.getValue(Product.class);
//                                    if (tmp.getStatus() == 1){
//                                        if (tmp.isEnable()) arr.add(tmp);
//                                        i += 1;
//                                    }
//                                    if (i == 8) break;
//                                }
//                                arr.sort(Comparator.comparing(Product::getTimestamp).reversed());
//                                mReference.child("Wishlist").orderByChild("userID").
//                                        equalTo(curUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        ArrayList<String> userLike = new ArrayList();
//                                        for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
//                                            Wishlist yP = appleSnapshot.getValue(Wishlist.class);
//                                            userLike.add(yP.getProID());
//                                        }
//                                        listViewProduct = view.findViewById(R.id.listViewProduct);
//                                        //Display adapter product
//                                        //Let Lon and lat attr of map in arraylist and add to last position ,otherwise let null if Mainactivty are on line
//                                        ArrayList<Double> lonlat = new ArrayList<>();
//                                        if ((MainActivity) getActivity() != null){
//                                            lonlat.add(((MainActivity) getActivity()).longitude);
//                                            lonlat.add(((MainActivity) getActivity()).latitude);
//                                        }else{
//                                            lonlat.add(10.3341779);
//                                            lonlat.add(107.077694);
//                                        }
//
//                                        ListViewAdapter listViewProductAdapter = new ListViewAdapter(arr,getActivity(),curUser,userLike,lonlat,curUser.getType());
//                                        listViewProduct.setAdapter(listViewProductAdapter);
//                                        listViewProduct.setLayoutManager(new LinearLayoutManager(curActivity));
//                                        pullToRefresh.setRefreshing(false);
//                                    }
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//            }
//        });
        //Display list product view
        return view;
    }
}
