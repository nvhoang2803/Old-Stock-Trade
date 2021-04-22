package com.example.oldstocktrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.oldstocktrade.Adapter.ProductAdapter;
import com.example.oldstocktrade.Model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SellProductActivity extends AppCompatActivity {
    private ImageButton btn_back;
    private RecyclerView recyclerView;
    private List<Product> mProducts;
    private RelativeLayout no_product;
    ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_product);

        btn_back = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.recycler);
        no_product = findViewById(R.id.no_product);
        Intent intent = getIntent();
        String buyerID = intent.getStringExtra("userid");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mProducts = new ArrayList<>();
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref_products = FirebaseDatabase.getInstance().getReference("Products");
        ref_products.orderByChild("Seller").equalTo(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mProducts.clear();
                for (DataSnapshot data : snapshot.getChildren()){
                    Product product = data.getValue(Product.class);
                    if (product.getStatus() == 1)
                        mProducts.add(product);
                }
                if (mProducts.size() != 0){
                    productAdapter = new ProductAdapter(SellProductActivity.this,mProducts, false, buyerID);
                    recyclerView.setLayoutManager(new GridLayoutManager(SellProductActivity.this,2));
                    recyclerView.setAdapter(productAdapter);
                    no_product.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                else {
                    no_product.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}