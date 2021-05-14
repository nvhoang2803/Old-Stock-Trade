package com.example.oldstocktrade.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.Model.Rating;
import com.example.oldstocktrade.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedbackActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;
    private CircleImageView product_image;
    private TextView product_name;
    private TextView product_price;
    private ImageButton btn_back;
    private Button btn_done;
    private ImageButton btn_star1;
    private ImageButton btn_star2;
    private ImageButton btn_star3;
    private ImageButton btn_star4;
    private ImageButton btn_star5;

    private EditText txt_feedback;
    private int rate = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initialize();
    }

    void initialize() {
        Intent i = getIntent();
        String proID = i.getStringExtra("proID");
        String userID = i.getStringExtra("userID");
        String feedbackID = i.getStringExtra("feedbackID");

        product_image = findViewById(R.id.product_image);
        product_name = findViewById(R.id.product_name);
        product_price = findViewById(R.id.product_price);
        btn_back = findViewById(R.id.btn_back);
        btn_done = findViewById(R.id.btn_done);
        btn_star1 = findViewById(R.id.star1);
        btn_star2 = findViewById(R.id.star2);
        btn_star3 = findViewById(R.id.star3);
        btn_star4 = findViewById(R.id.star4);
        btn_star5 = findViewById(R.id.star5);
        txt_feedback = findViewById(R.id.txt_feedback);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("Products").child(proID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                product_name.setText(product.getName());
                product_price.setText(product.getPrice()+"");
                Glide.with(FeedbackActivity.this).load(product.getImageURL().get(0)).into(product_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_star1.setBackgroundResource(R.drawable.ic_star_full);
                btn_star2.setBackgroundResource(R.drawable.ic_star);
                btn_star3.setBackgroundResource(R.drawable.ic_star);
                btn_star4.setBackgroundResource(R.drawable.ic_star);
                btn_star5.setBackgroundResource(R.drawable.ic_star);
                rate = 1;
            }
        });
        btn_star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_star1.setBackgroundResource(R.drawable.ic_star_full);
                btn_star2.setBackgroundResource(R.drawable.ic_star_full);
                btn_star3.setBackgroundResource(R.drawable.ic_star);
                btn_star4.setBackgroundResource(R.drawable.ic_star);
                btn_star5.setBackgroundResource(R.drawable.ic_star);
                rate = 2;
            }
        });
        btn_star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_star1.setBackgroundResource(R.drawable.ic_star_full);
                btn_star2.setBackgroundResource(R.drawable.ic_star_full);
                btn_star3.setBackgroundResource(R.drawable.ic_star_full);
                btn_star4.setBackgroundResource(R.drawable.ic_star);
                btn_star5.setBackgroundResource(R.drawable.ic_star);
                rate = 3;
            }
        });
        btn_star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_star1.setBackgroundResource(R.drawable.ic_star_full);
                btn_star2.setBackgroundResource(R.drawable.ic_star_full);
                btn_star3.setBackgroundResource(R.drawable.ic_star_full);
                btn_star4.setBackgroundResource(R.drawable.ic_star_full);
                btn_star5.setBackgroundResource(R.drawable.ic_star);
                rate = 4;
            }
        });
        btn_star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_star1.setBackgroundResource(R.drawable.ic_star_full);
                btn_star2.setBackgroundResource(R.drawable.ic_star_full);
                btn_star3.setBackgroundResource(R.drawable.ic_star_full);
                btn_star4.setBackgroundResource(R.drawable.ic_star_full);
                btn_star5.setBackgroundResource(R.drawable.ic_star_full);
                rate = 5;
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rate == -1)
                    Toast.makeText(FeedbackActivity.this,"You haven't done yet!",Toast.LENGTH_SHORT).show();
                else {
                    //Add rating to user
                    DatabaseReference new_feedback = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Ratings").push();
                    Rating rating = new Rating(new_feedback.getKey(),txt_feedback.getText().toString(),rate,firebaseUser.getUid());
                    new_feedback.setValue(rating);
                    FirebaseDatabase.getInstance().getReference().child("Feedback").child(firebaseUser.getUid()).child(feedbackID).removeValue();
                    Toast.makeText(FeedbackActivity.this,"Feedback successfully",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}