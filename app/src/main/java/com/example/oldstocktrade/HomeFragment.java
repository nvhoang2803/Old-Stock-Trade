package com.example.oldstocktrade;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.oldstocktrade.Adapter.ListViewAdapter;
import com.example.oldstocktrade.Adapter.ListViewCommentAdapter;
import com.example.oldstocktrade.Adapter.SearchFillAdapter;
import com.example.oldstocktrade.Model.Comment;
import com.example.oldstocktrade.Model.MyProduct;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Model.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

    Activity curActivity;

    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    User curUser;

    public HomeFragment(Activity act){
        curActivity = act;
    }

    public void setSearchFill(View view){
        searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint("Search View");
        //

        //

        searchFill = view.findViewById(R.id.searchFill);
        //
        searchfillAdapter = new SearchFillAdapter(searcharr);
        searchFill.setAdapter(searchfillAdapter);
        //Handle on click on search
        searchFill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currQuerySearch = searcharr.get(position);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchfillAdapter.filter(newText);
                searchfillAdapter.notifyDataSetChanged();
                return true;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    searchFill.setVisibility(View.VISIBLE);
                }else{
                    searchFill.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Catch event on [x] button inside search view
        View closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);

        EditText searchEditText = (EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.BLACK);
        searchEditText.setHintTextColor(Color.LTGRAY);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setVisibility(View.GONE);
            }
        });
        //
        searchView.setBackgroundColor(Color.WHITE);
    }

    public void handleSearch(View view){
        setSearchFill(view);
        ImageView bntSearch = view.findViewById(R.id.btnSearch);
        bntSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReference.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        searcharr.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            Product tmp = ds.getValue(Product.class);
                            searcharr.add(tmp.getName());
                        }
                        searchfillAdapter = new SearchFillAdapter(searcharr);
                        searchFill.setAdapter(searchfillAdapter);
                        searchView = view.findViewById(R.id.searchView);
                        searchView.setIconifiedByDefault(false);
                        searchView.setFocusable(true);
                        searchView.setIconified(false);
                        searchView.requestFocusFromTouch();
                        searchView.setVisibility(View.VISIBLE);

                        searchView.bringToFront();
                        searchFill.setVisibility(View.VISIBLE);
                        searchFill.bringToFront();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
        mReference.child("Users").orderByChild("id")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    curUser = ds.getValue(User.class);
                }
                mReference.child("Products").limitToLast(10).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product tmp;
                        for (DataSnapshot ds: snapshot.getChildren()){
                            tmp = ds.getValue(Product.class);
                            if (tmp.getStatus() == 1){
                                arr.add(tmp);
                            }
                        }
                        arr.sort(Comparator.comparing(Product::getTimestamp));
                        mReference.child("MyProducts").orderByChild("userID").
                                equalTo(curUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<String> userLike = new ArrayList();
                                for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                                    MyProduct yP = appleSnapshot.getValue(MyProduct.class);
                                    userLike.add(yP.getProID());
                                }
                                listViewProduct = view.findViewById(R.id.listViewProduct);
                                //Display adapter product
                                ListViewAdapter listViewProductAdapter = new ListViewAdapter(arr,curActivity,curUser,userLike);
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
                mReference.child("Products").limitToLast(10).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Product tmp;
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    tmp = ds.getValue(Product.class);
                                    if (tmp.getStatus() == 1){
                                        arr.add(tmp);
                                    }
                                }
                                arr.sort(Comparator.comparing(Product::getTimestamp));
                                mReference.child("MyProducts").orderByChild("userID").
                                        equalTo(curUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ArrayList<String> userLike = new ArrayList();
                                        for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                                            MyProduct yP = appleSnapshot.getValue(MyProduct.class);
                                            userLike.add(yP.getProID());
                                        }
                                        listViewProduct = view.findViewById(R.id.listViewProduct);
                                        //Display adapter product
                                        ListViewAdapter listViewProductAdapter = new ListViewAdapter(arr,curActivity,curUser,userLike);
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

        //Display list product view




        return view;
    }
}
