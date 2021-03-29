package com.example.oldstocktrade;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.oldstocktrade.Adapter.ListViewAdapter;
import com.example.oldstocktrade.Adapter.SearchFillAdapter;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.Model.Wishlist;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;

public class SearchSortActivity extends AppCompatActivity {
    RecyclerView listViewProduct;
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<Product> arr;
    SearchView searchView;
    SearchFillAdapter searchfillAdapter;
    ListView searchFill;
    User curUser;
    ArrayList<String> searcharr;
    //

    String curQuery;
    LinearLayout sortField;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sort);
        sortField = findViewById(R.id.sortfield_ass);
        String searchQuery;
        double lon = 0;
        double lat = 0;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                searchQuery= "";
            } else {
                searchQuery= extras.getString("searchQuery");
                lon= extras.getDouble("lon");
                lat= extras.getDouble("lat");
            }
        } else {
            searchQuery= (String) savedInstanceState.getSerializable("searchQuery");
            lon= (Double) savedInstanceState.getSerializable("lon");
            lat= (Double) savedInstanceState.getSerializable("lat");
        }
        arr = new ArrayList<>();
        searcharr = new ArrayList<>();
        double finalLon = lon;
        double finalLat = lat;
        mReference.child("Users").orderByChild("id")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    curUser = ds.getValue(User.class);
                }
                mReference.child("Products").limitToLast(10).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Product tmp;
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    tmp = ds.getValue(Product.class);
                                    searcharr.add(tmp.getName());
                                    if (tmp.getStatus() == 1){
                                        arr.add(tmp);
                                        searcharr.add(tmp.getName());
                                    }
                                }
                                arr.sort(Comparator.comparing(Product::getTimestamp));
                                mReference.child("Wishlist").orderByChild("userID").
                                        equalTo(curUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ArrayList<String> userLike = new ArrayList();
                                        for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                                            Wishlist yP = appleSnapshot.getValue(Wishlist.class);
                                            userLike.add(yP.getProID());
                                        }
                                        listViewProduct = findViewById(R.id.listViewProduct_ass);
                                        //Display adapter product
                                        ArrayList<Double> lonlat = new ArrayList<>();
                                        lonlat.add(finalLon);
                                        lonlat.add(finalLat);
                                        ListViewAdapter listViewProductAdapter = new ListViewAdapter(arr,SearchSortActivity.this,curUser,userLike,lonlat);
                                        listViewProduct.setAdapter(listViewProductAdapter);
                                        listViewProduct.setLayoutManager(new LinearLayoutManager(SearchSortActivity.this));

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
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.refreshProductView_ass);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arr.clear();
                mReference.child("Products").limitToLast(10).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
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
                                mReference.child("Wishlist").orderByChild("userID").
                                        equalTo(curUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ArrayList<String> userLike = new ArrayList();
                                        for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                                            Wishlist yP = appleSnapshot.getValue(Wishlist.class);
                                            userLike.add(yP.getProID());
                                        }
                                        listViewProduct = findViewById(R.id.listViewProduct_ass);
                                        //Display adapter product
                                        //Let Lon and lat attr of map in arraylist and add to last position ,otherwise let null if Mainactivty are on line
                                        ArrayList<Double> lonlat = new ArrayList<>();
                                        lonlat.add(finalLon);
                                        lonlat.add(finalLat);
                                        ListViewAdapter listViewProductAdapter = new ListViewAdapter(arr,SearchSortActivity.this,curUser,userLike,lonlat);
                                        listViewProduct.setAdapter(listViewProductAdapter);
                                        listViewProduct.setLayoutManager(new LinearLayoutManager(SearchSortActivity.this));
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
        //

        searchView = findViewById(R.id.searchView_ass);
        searchView.getFocusable();
        searchFill = findViewById(R.id.searchFill_ass);
        handleSearch();
        //
        ImageView btnback = findViewById(R.id.btnBack);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Handle Sort
        handleSort();

    }

    public void handleSort(){
        LinearLayout sortField_1 = findViewById(R.id.sort1_ass);
        LinearLayout sortField_2 = findViewById(R.id.sort2_ass);
        LinearLayout sortField_3 = findViewById(R.id.sort3_ass);


        sortField_1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                SeekBar seekBarDistance = new SeekBar(SearchSortActivity.this);
                seekBarDistance.setMin(0);
                seekBarDistance.setMax(200);
                //LinearLayout.LayoutParams seekBarLayout = ;
                //seekBarLayout.setMargins(0,20,0,0);
                seekBarDistance.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                TextView txt = new TextView(SearchSortActivity.this);
                txt.setText("Distance");
                txt.setTextColor(Color.BLACK);


                LinearLayout ln = new LinearLayout(SearchSortActivity.this);
                TextView max = new TextView(SearchSortActivity.this);
                max.setText("0");
                TextView curr = new TextView(SearchSortActivity.this);
                curr.setText("");
                TextView min = new TextView(SearchSortActivity.this);
                min.setText("200");

                ln.addView(min);
                ln.addView(curr);
                ln.addView(min);

                LinearLayout.LayoutParams lnLayout = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lnLayout.setMargins(0,5,0,15);
                ln.setOrientation(LinearLayout.HORIZONTAL);
                ln.setLayoutParams(lnLayout);

                txt.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                //txt.setGravity(Gravity.RIGHT);

                sortField.addView(txt);
                sortField.addView(ln);
                sortField.addView(seekBarDistance);
                sortField.setVisibility(View.VISIBLE);
                sortField.bringToFront();
            }
        });
    }

    public void handleSearch(){
        searchfillAdapter = new SearchFillAdapter(searcharr);
        searchFill.setAdapter(searchfillAdapter);
        //Handle on click on search
        searchFill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        final Handler handler = new Handler();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mReference.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                searcharr.clear();
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    Product tmp = ds.getValue(Product.class);
                                    if (tmp.getName().contains(searchView.getQuery().toString())){
                                        searcharr.add(tmp.getName());
                                    }

                                }
                                searchfillAdapter = new SearchFillAdapter(searcharr);
                                searchFill.setAdapter(searchfillAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }, 500);
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    searchFill.setVisibility(View.VISIBLE);
                    searchFill.bringToFront();
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
                searchFill.setVisibility(View.GONE);
                searchView.setQuery("",false);
                searchView.clearFocus();
            }
        });
        //
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
    }




}