package com.example.oldstocktrade;

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

import com.example.oldstocktrade.Adapter.ListViewAdapter;
import com.example.oldstocktrade.Adapter.SearchFillAdapter;
import com.example.oldstocktrade.Model.Product;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    ArrayList<Product> arr;
    SearchView searchView;
    ListView searchFill;
    ListView listViewProduct;
    ArrayList<String> searcharr;
    //
    SeekBar priceSlider;
    SeekBar distanceSlider;
    SeekBar ratingSlider;

    public void setSearchFill(View view){
        searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint("Search View");
        searcharr  = new ArrayList<String>();
        searcharr.add("N");
        searcharr.add("A");
        searcharr.add("B");
        searcharr.add("D");
        //

        //
        final SearchFillAdapter searchfillAdapter = new SearchFillAdapter(searcharr);
        searchFill = view.findViewById(R.id.searchFill);
        //
        searchFill.setAdapter(searchfillAdapter);
        //Handle on click on search
        searchFill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
                searchView.setVisibility(View.INVISIBLE);
            }
        });
        //
        searchView.setBackgroundColor(Color.WHITE);
        searchView.setVisibility(View.INVISIBLE);
        searchFill.bringToFront();
        searchFill.setVisibility(View.INVISIBLE);
    }

    public void handleSearch(View view){
        setSearchFill(view);
        ImageView bntSearch = view.findViewById(R.id.btnSearch);
        bntSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView = view.findViewById(R.id.searchView);
                searchView.setIconifiedByDefault(false);
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
                searchView.setVisibility(View.VISIBLE);
                searchView.bringToFront();
            }
        });
    }

    public void sortProductView(){
        listViewProduct.notifyAll();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleSort(View view){
        final ConstraintLayout sortView = view.findViewById(R.id.sortView);
        ImageView bntSort = view.findViewById(R.id.bntSort);
        bntSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortView.setVisibility(View.VISIBLE);
            }
        });

        Button btnSort = view.findViewById(R.id.Sort);
        Button btnClose = view.findViewById(R.id.Close);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortView.setVisibility(View.INVISIBLE);
            }
        });
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call function and invisible view
                sortView.setVisibility(View.INVISIBLE);
            }
        });

        priceSlider = view.findViewById(R.id.sliderPrice);
        TextView txtPrice = view.findViewById(R.id.priceValue);
        //
        distanceSlider = view.findViewById(R.id.sliderDistance);
        TextView txtDistance = view.findViewById(R.id.distanceValue);
        //
        ratingSlider = view.findViewById(R.id.sliderRating);
        TextView txtRating = view.findViewById(R.id.ratingValue);
        handleSliderInput(priceSlider,txtPrice,200000,50000000,"$");
        handleSliderInput(distanceSlider,txtDistance,2,100,"km");
        handleSliderInput(ratingSlider,txtRating,0,5,"");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleSliderInput(SeekBar seekBar, final TextView txtValue, int min, int max, final String text){
        seekBar.setMax(max);
        seekBar.setMin(min);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress;
                txtValue.setText(progress+ text);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_new_feed, container, false);

        arr = new ArrayList<>();
        arr.add(new Product("KO oc tidlsds",4));
        arr.add(new Product("KOdsdsds fdewfeg dd",4));
        //Display list product view
        listViewProduct = view.findViewById(R.id.listViewProduct);
        //Display adapter product
        ListViewAdapter listViewProductAdapter = new ListViewAdapter(arr);
        listViewProduct.setAdapter(listViewProductAdapter);

        handleSearch(view);
        handleSort(view);


        return view;
    }
}
