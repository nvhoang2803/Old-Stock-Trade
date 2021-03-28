package com.example.oldstocktrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView profile_image;
    private TextView username;
    private EditText txt_phone;
    private EditText txt_address;
    private FirebaseUser fuser;
    private DatabaseReference ref;
    private Button btn_cancel;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        txt_phone  = findViewById(R.id.txt_phone);
        txt_address  = findViewById(R.id.txt_address);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user.getImageURL().equals("default"))
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                else Glide.with(ProfileActivity.this).load(user.getImageURL()).into(profile_image);
                username.setText(user.getUsername());
                txt_phone.setText(user.getPhone());
                txt_address.setText(user.getAddress());
                txt_phone.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        btn_save.setVisibility(View.GONE);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        btn_save.setVisibility(View.VISIBLE);
                    }
                });
                txt_address.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        btn_save.setVisibility(View.GONE);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        btn_save.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = txt_phone.getText().toString();
                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(ProfileActivity.this, "Your phone number cannot be empty!",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (phone.length() == 10 && TextUtils.isDigitsOnly(phone)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("phone",txt_phone.getText().toString());
                        hashMap.put("address",txt_address.getText().toString());

                        ref.updateChildren(hashMap);
                        Toast.makeText(ProfileActivity.this, "Valid",Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Toast.makeText(ProfileActivity.this, "Your phone number is invalid.",Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

    }
    ValueEventListener valueEventListener = null;

    @Override
    protected void onPause() {
        super.onPause();
        ref.removeEventListener(valueEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ref.addValueEventListener(valueEventListener);

    }
}