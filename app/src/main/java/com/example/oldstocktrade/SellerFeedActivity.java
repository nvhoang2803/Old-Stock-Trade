package com.example.oldstocktrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Adapter.ProductAdapter;
import com.example.oldstocktrade.Adapter.StorageAdapter;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerFeedActivity extends AppCompatActivity {
    private CircleImageView user_image;
    private TextView username;
    private TextView status;
    private TextView product_amount;
    private RecyclerView recyclerView;
    private String userid;
    private List<Product> mProducts;
    private ImageButton btn_back;
    ProductAdapter productAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_feed);

        user_image = findViewById(R.id.user_image);
        username = findViewById(R.id.username);
        status = findViewById(R.id.status);
        product_amount = findViewById(R.id.product_amount);
        btn_back = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.recycler);

        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        mProducts = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user.getImageURL().equals("default"))
                    user_image.setImageResource(R.mipmap.ic_launcher);
                else Glide.with(SellerFeedActivity.this).load(user.getImageURL()).into(user_image);
                username.setText(user.getUsername());
                if (user.getStatus().equals("online"))
                    status.setText("Online");
                else status.setText("Offline");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                product_amount.setText(mProducts.size() +"");
                productAdapter = new ProductAdapter(SellerFeedActivity.this,mProducts, true, null);
                recyclerView.setLayoutManager(new GridLayoutManager(SellerFeedActivity.this,2));
                recyclerView.setAdapter(productAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}