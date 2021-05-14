
package com.example.oldstocktrade.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.oldstocktrade.Adapter.ListViewAdapter;
import com.example.oldstocktrade.Adapter.SearchFillAdapter;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.Model.Wishlist;
import com.example.oldstocktrade.R;
import com.example.oldstocktrade.Utils.VnCharacteristic;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    public double finalLon;
    public double finalLat;
    public String finalAddress;
    String curQuery;
    LinearLayout sortField;
    //
    RadioButton rb;
    RangeSlider priceSlider;
    RangeSlider distanceSlider;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sort);
        sortField = findViewById(R.id.sortfield_ass);
        String searchQuery;
        double lon = 0;
        double lat = 0;
        String address;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                searchQuery= "";
            } else {
                searchQuery= extras.getString("searchQuery");
                lon= extras.getDouble("lon");
                lat= extras.getDouble("lat");
                address=extras.getString("address");
            }
        } else {
            searchQuery= (String) savedInstanceState.getSerializable("searchQuery");
            lon= (Double) savedInstanceState.getSerializable("lon");
            lat= (Double) savedInstanceState.getSerializable("lat");
            address=(String)savedInstanceState.getSerializable("address");
        }
        arr = new ArrayList<>();
        searcharr = new ArrayList<>();
        finalLon = lon;
        finalLat = lat;
        mReference.child("Users").orderByChild("id")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    curUser = ds.getValue(User.class);
                }
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
                                        resetSortSpecificField();
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
        //
        LinearLayout sortFilter = findViewById(R.id.sortfilter_ass);
        sortFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SearchSortActivity.this,R.style.BottomSheetDialogdTheme);
                View bottomShettView = LayoutInflater.from(SearchSortActivity.this).inflate(R.layout.sort_feld_ass,
                        findViewById(R.id.sort_feld_ass_container));
                int height = getWindowHeight();
                //((ConstraintLayout) bottomShettView.findViewById(R.id.sort_feld_ass_container)).setLayoutParams(
                //new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,height));
                //Handle Sort
                bottomSheetDialog.setContentView(bottomShettView);
                handleSort(bottomShettView,bottomSheetDialog);
                //setupFullHeight(bottomSheetDialog);
                bottomSheetDialog.show();
            }
        });

        handleSortSpecificField();
        //Handle Sort Layout
    }
    public void resetSortSpecificField(){
        ((TextView) findViewById(R.id.sort2_ass_txt)).setText("Price");
        ((TextView) findViewById(R.id.sort1_ass_txt)).setText("Time");
        ((TextView) findViewById(R.id.sort2_ass_txt)).setTextColor(Color.BLACK);
        ((TextView) findViewById(R.id.sort1_ass_txt)).setTextColor(Color.BLACK);
        LinearLayout specificPrice = findViewById(R.id.sort2_ass);

        specificPrice.setBackground(ContextCompat.getDrawable(SearchSortActivity.this, R.drawable.background_search));
        LinearLayout specificTime = findViewById(R.id.sort1_ass);
        specificTime.setBackground(ContextCompat.getDrawable(SearchSortActivity.this, R.drawable.background_search));
    }
    public void handleSortSpecificField(){
        LinearLayout specificPrice = findViewById(R.id.sort2_ass);
        specificPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SearchSortActivity.this,R.style.BottomSheetDialogdTheme);
                View bottomShettView = LayoutInflater.from(SearchSortActivity.this).inflate(R.layout.sort_feld__price_ass,
                        SearchSortActivity.this.findViewById(R.id.sort_feld__price_ass_container));
                bottomSheetDialog.setContentView(bottomShettView);
                bottomSheetDialog.show();
                Button btn = bottomShettView.findViewById(R.id.button);

                RadioGroup rg = (RadioGroup)
                        bottomShettView.findViewById(R.id.specific_sort_feld__sort_ass_container);
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton tmp = (RadioButton) bottomShettView.findViewById(checkedId);
                        String query = tmp.getText().toString();
                        specificPrice.setBackground(ContextCompat.getDrawable(SearchSortActivity.this, R.drawable.background_specific_sort));
                        TextView txt = (TextView) findViewById(R.id.sort2_ass_txt);
                        txt.setText(query);
                        txt.setTextColor(Color.WHITE);
                        ArrayList<String> arrFillSortCondition = new ArrayList<>();
                        arrFillSortCondition.add(query); //Just sort price
                        arrFillSortCondition.add("");
                        arrFillSortCondition.add("");
                        fillSort(arrFillSortCondition,"");
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });
        LinearLayout specificTime = findViewById(R.id.sort1_ass);
        specificTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SearchSortActivity.this,R.style.BottomSheetDialogdTheme);
                View bottomShettView = LayoutInflater.from(SearchSortActivity.this).inflate(R.layout.sort_feld_distance_ass,
                        SearchSortActivity.this.findViewById(R.id.sort_feld__price_ass_container));
                bottomSheetDialog.setContentView(bottomShettView);
                bottomSheetDialog.show();

                RadioGroup rg = (RadioGroup)
                        bottomShettView.findViewById(R.id.specific_sort_feld__distance_ass_container);

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton tmp = (RadioButton) bottomShettView.findViewById(checkedId);
                        String query = tmp.getText().toString();
                        specificTime.setBackground(ContextCompat.getDrawable(SearchSortActivity.this, R.drawable.background_specific_sort));
                        TextView txt = (TextView) findViewById(R.id.sort1_ass_txt);
                        txt.setText(query);
                        txt.setTextColor(Color.WHITE);
                        ArrayList<String> arrFillSortCondition = new ArrayList<>();
                        arrFillSortCondition.add(""); //Just sort price
                        arrFillSortCondition.add(query);
                        arrFillSortCondition.add("");
                        fillSort(arrFillSortCondition,"");
                        bottomSheetDialog.dismiss();
                    }
                });

            }
        });
    }
    //Fill sort and updated data follow Sort field
    public void fillSort(ArrayList<String> arrSort,String type){
        mReference.child("Products").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product tmp;
                        arr.clear();

                        String query = searchView.getQuery().toString();
                        if (type == "Filter"){
                            List<Float> priceSort = priceSlider.getValues();
                            for (DataSnapshot ds: snapshot.getChildren()){
                                tmp = ds.getValue(Product.class);
                                if ((tmp.getPrice() > priceSort.get(0) * 1000) &&
                                        (tmp.getPrice() < priceSort.get(1) * 1000) && tmp.getName() != null && tmp.getStatus() == 1
                                            && (tmp.getName() != null && (( tmp.getName().toLowerCase().contains(query.toLowerCase())
                                        || VnCharacteristic.removeAccent(tmp.getName()).toLowerCase().contains(query.toLowerCase()) )) ||
                                                ( tmp.getDescription().toLowerCase().contains(query.toLowerCase())
                                                        || VnCharacteristic.removeAccent(tmp.getDescription()).toLowerCase().contains(query.toLowerCase()) ))){
                                    arr.add(tmp);
                                }
                            }
                        }else {
                            for (DataSnapshot ds: snapshot.getChildren()){
                                tmp = ds.getValue(Product.class);
                                if (tmp.getName() != null && tmp.getStatus() == 1 && ((( tmp.getName().toLowerCase().contains(query.toLowerCase())
                                        || VnCharacteristic.removeAccent(tmp.getName()).toLowerCase().contains(query.toLowerCase()) )) ||
                                        ( tmp.getDescription().toLowerCase().contains(query.toLowerCase())
                                                || VnCharacteristic.removeAccent(tmp.getDescription()).toLowerCase().contains(query.toLowerCase())) )){
                                    arr.add(tmp);
                                }
                                System.out.println(tmp.getName());
                            }
                        }
                        if (arrSort.get(0).toString().contains("From Low to High")){
                            arr.sort(Comparator.comparing(Product::getPrice));
                        }else if (arrSort.get(0).toString().contains("From High to Low")){
                            arr.sort(Comparator.comparing(Product::getPrice).reversed());
                        }
                        if (arrSort.get(1).toString().contains("From Oldest to Newest")){
                            if (type == "Filter"){
                                Collections.sort(arr, (Comparator<Product>) (lhs, rhs) -> {
                                    if ((lhs.getPrice() == rhs.getPrice())){
                                        if (lhs.getTimestamp() > rhs.getPrice()){
                                            return 1;
                                        }else {
                                            return -1;
                                        }
                                    }
                                    return 0;
                                });
                            }else{
                                arr.sort(Comparator.comparing(Product::getTimestamp));
                            }
                        }else if (arrSort.get(1).toString().contains("From Newest to Oldest")){
                            if (type == "Filter"){
                                Collections.sort(arr, (Comparator<Product>) (lhs, rhs) -> {
                                    if ((lhs.getPrice() == rhs.getPrice())){
                                        if (lhs.getTimestamp() > rhs.getPrice()){
                                            return -1;
                                        }else {
                                            return 1;
                                        }
                                    }
                                    return 0;
                                });
                            }else{
                                arr.sort(Comparator.comparing(Product::getTimestamp).reversed());
                            }
                        }
//                        if (arrSort.get(2).toString().contains("From High to Low")){
//                            Collections.sort(arr, (Comparator<Product>) (lhs, rhs) -> {
//                                if ((lhs.getPrice() == rhs.getPrice()){
//                                    if (lhs.getTimestamp() > rhs.getPrice()){
//                                        return 1;
//                                    }else {
//                                        return -1;
//                                    }
//                                }
//                                return 0;
//                            });
//                        }else if (arrSort.get(2).toString().contains("From Low to High")){
//                            arr.sort(Comparator.comparing(Product::getRate).reversed());
//                        }
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


    public String ChangeMoneyToString(int price){
        int cur;
        String priceD = "";
        String result = "";
        priceD = String.valueOf(price);
        cur = priceD.length() - 3;
        for (int i =0 ; i< ((int)(priceD.length() / 3));i++ ){
            result = "." + priceD.substring(cur,cur +3)+ result;
            cur = cur -3;
        }
        cur = cur + 3;
        result = priceD.substring(0, cur) + result;
        return result;
    }

    public void handleSort(View viewS,BottomSheetDialog bottomSheetDialog){
        priceSlider = viewS.findViewById(R.id.sortfield_ass_priceSlider);
        priceSlider.setValueFrom(0);
        priceSlider.setValueTo(50000);
        priceSlider.setValues(0f,1000f);
        TextView priceSliderValue = viewS.findViewById(R.id.sortfield_ass_priceSliderValue);
        priceSliderValue.setText("Price from :" + 0 + " to: " + "1.000.000");
        priceSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                priceSliderValue.setText(" " + ChangeMoneyToString((int) Math.floor(priceSlider.getValues().get(0)) * 1000) + "VND to: "
                 + ChangeMoneyToString((int) Math.floor(priceSlider.getValues().get(1)) * 1000) + "VND");

            }
        });
        ImageView btnClose = viewS.findViewById(R.id.sortfield_ass_btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                searchView.clearFocus();
            }
        });
        TextView btnReset = viewS.findViewById(R.id.sortfield_ass_btnReset);
        Button btnShow = viewS.findViewById(R.id.sortfield_ass_btnShow);
        RadioGroup radioGroup_Sort = viewS.findViewById(R.id.sort_feld__price_ass_container);
        RadioGroup radioGroup_Distance = viewS.findViewById(R.id.specific_sort_feld__distance_ass_container);
        RadioGroup radioGroup_Rate = viewS.findViewById(R.id.specific_sort_feld__rating_ass_container);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroup_Distance.clearCheck();
                radioGroup_Rate.clearCheck();
                radioGroup_Sort.clearCheck();
                priceSlider.setValues(0f,200000f);
                distanceSlider.setValues(0f,80f);
            }
        });


        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> arr = new ArrayList<>();
                int selectedId = radioGroup_Sort.getCheckedRadioButtonId();
                rb = (RadioButton) viewS.findViewById(selectedId);
                arr.add(rb.getText().toString());
                selectedId = radioGroup_Distance.getCheckedRadioButtonId();
                rb = (RadioButton) viewS.findViewById(selectedId);
                arr.add(rb.getText().toString());
                selectedId = radioGroup_Rate.getCheckedRadioButtonId();
                rb = (RadioButton) viewS.findViewById(selectedId);
                arr.add(rb.getText().toString());
                fillSort(arr,"Filter");
                bottomSheetDialog.dismiss();
                resetSortSpecificField();
                searchView.clearFocus();
            }
        });

    }
    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        int windowHeight = getWindowHeight();
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                windowHeight);
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        (SearchSortActivity.this).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
    public void handleSearch(){
        searchfillAdapter = new SearchFillAdapter(searcharr);
        searchFill.setAdapter(searchfillAdapter);
        //Handle on click on search
        searchFill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery(searcharr.get(position),true);
            }
        });
        final Handler handler = new Handler();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mReference.child("Products").
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                arr.clear();
                                Product tmp;
                                int i=0;
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    tmp = ds.getValue(Product.class);
                                    if (tmp.getName() != null && tmp.getStatus() == 1 && ((( tmp.getName().toLowerCase().contains(query.toLowerCase())
                                            || VnCharacteristic.removeAccent(tmp.getName()).toLowerCase().contains(query.toLowerCase()) )
                                                || ( tmp.getDescription().toLowerCase().contains(query.toLowerCase())
                                            || VnCharacteristic.removeAccent(tmp.getDescription()).toLowerCase().contains(query.toLowerCase()) )))){
                                        arr.add(tmp);
                                        i+=1;
                                    }
                                    if (i == 8) break;
                                }
                                arr.sort(Comparator.comparing(Product::getStatus).reversed());
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
                searchFill.setVisibility(View.GONE);
                searchView.clearFocus();
                return true;
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
                                    String txt = tmp.getName();
                                    if (txt != null && tmp.getStatus() == 1 && ((( tmp.getName().toLowerCase().contains(newText.toLowerCase())
                                            || VnCharacteristic.removeAccent(tmp.getName()).toLowerCase().contains(newText.toLowerCase()) )) ||
                                            ( tmp.getDescription().toLowerCase().contains(newText.toLowerCase())
                                                    || VnCharacteristic.removeAccent(tmp.getDescription()).toLowerCase().contains(newText.toLowerCase()) ))   ){
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
                resetSortSpecificField();
                searchView.setQuery("",false);
                searchView.clearFocus();
                mReference.child("Products").limitToLast(10).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Product tmp;
                                arr.clear();
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
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
    }


}