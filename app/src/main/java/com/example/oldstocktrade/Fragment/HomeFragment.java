package com.example.oldstocktrade.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.oldstocktrade.Activity.PostActivity;
import com.example.oldstocktrade.Activity.SearchSortActivity;
import com.example.oldstocktrade.Adapter.ListViewAdapter;
import com.example.oldstocktrade.Adapter.SearchFillAdapter;
import com.example.oldstocktrade.Activity.MainActivity;
import com.example.oldstocktrade.Model.Wishlist;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;

public class HomeFragment extends Fragment {

    ArrayList<Product> arr;
    SearchView searchView;
    ListView searchFill;
    RecyclerView listViewProduct;
    ArrayList<String> searcharr;
    //
    SearchFillAdapter searchfillAdapter;

    MainActivity curActivity;

    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    User curUser;

    public HomeFragment(MainActivity act){
        curActivity = act;
    }

    public void handleSearch(View view){
        ImageView bntSearch = view.findViewById(R.id.btnSearch);
        bntSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(curActivity, SearchSortActivity.class);
                intent.putExtra("searchQuery", "Search");
                intent.putExtra("lon", ((MainActivity) curActivity).longitude);
                intent.putExtra("lat",  ((MainActivity) curActivity).latitude);
                intent.putExtra("address",  ((MainActivity) curActivity).address);
                startActivity(intent);

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_new_feed, container, false);
        arr = new ArrayList<>();
        searcharr = new ArrayList<>();
        Log.d("Begin activity", "onCreateView: "+FirebaseAuth.getInstance().getCurrentUser().getUid());
        mReference.child("Users").orderByChild("id")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    curUser = ds.getValue(User.class);
                }
                mReference.child("Products").orderByChild("Timestamp").
                        addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product tmp;
                        int i=0;
                        for (DataSnapshot ds: snapshot.getChildren()){
                            tmp = ds.getValue(Product.class);
                            if (tmp.getStatus() == 1){
                                if (tmp.isEnable()) arr.add(tmp);
                                i+=1;
                            }
                            if (i==12) break;
                        }
                        arr.sort(Comparator.comparing(Product::getTimestamp).reversed());
                        mReference.child("Wishlist").orderByChild("userID").
                                equalTo(curUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<String> userLike = new ArrayList();
                                for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                                    Wishlist yP = appleSnapshot.getValue(Wishlist.class);
                                    userLike.add(yP.getProID());
                                }
                                listViewProduct = view.findViewById(R.id.listViewProduct);
                                //Display adapter product
                                //Let Lon and lat attr of map in arraylist and add to last position ,otherwise let null if Mainactivty are on line
                                ListViewAdapter listViewProductAdapter = new ListViewAdapter(arr,curActivity,curUser,userLike,null,curUser.getType());
                                listViewProduct.setAdapter(listViewProductAdapter);
                                listViewProduct.setLayoutManager(new LinearLayoutManager(curActivity));
                                handleSearch(view);
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.refreshProductView);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arr.clear();
                mReference.child("Products").orderByChild("Timestamp").
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Product tmp;
                                int i = 0;
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    tmp = ds.getValue(Product.class);
                                    if (tmp.getStatus() == 1){
                                        if (tmp.isEnable()) {
                                            arr.add(tmp);
                                            i += 1;
                                        }

                                    }
                                    if (i == 12) break;
                                }
                                arr.sort(Comparator.comparing(Product::getTimestamp).reversed());
                                mReference.child("Wishlist").orderByChild("userID").
                                        equalTo(curUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ArrayList<String> userLike = new ArrayList();
                                        for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                                            Wishlist yP = appleSnapshot.getValue(Wishlist.class);
                                            userLike.add(yP.getProID());
                                        }
                                        listViewProduct = view.findViewById(R.id.listViewProduct);
                                        //Display adapter product
                                        //Let Lon and lat attr of map in arraylist and add to last position ,otherwise let null if Mainactivty are on line
                                        ArrayList<Double> lonlat = new ArrayList<>();
                                        if ((MainActivity) getActivity() != null){
                                            lonlat.add(((MainActivity) getActivity()).longitude);
                                            lonlat.add(((MainActivity) getActivity()).latitude);
                                        }else{
                                            lonlat.add(10.3341779);
                                            lonlat.add(107.077694);
                                        }

                                        ListViewAdapter listViewProductAdapter = new ListViewAdapter(arr,getActivity(),curUser,userLike,lonlat,curUser.getType());
                                        listViewProduct.setAdapter(listViewProductAdapter);
                                        listViewProduct.setLayoutManager(new LinearLayoutManager(curActivity));
                                        pullToRefresh.setRefreshing(false);
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
            }
        });
        ImageView btnPost = view.findViewById(R.id.btnPost);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putDouble ("lat",((MainActivity) curActivity).latitude);
                myBundle.putDouble ("long",((MainActivity) curActivity).longitude);
                intent.putExtras(myBundle);
                startActivity(intent);
            }
        });
        //Display list product view
        return view;
    }
}
