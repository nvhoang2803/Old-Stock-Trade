package com.example.oldstocktrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Adapter.ViewPageAdapter;
import com.example.oldstocktrade.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;
    private CircleImageView user_image;
    private TextView username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        tabLayout = findViewById(R.id.tab_contact);
        viewPager = findViewById(R.id.viewpager_id);
        adapter = new ViewPageAdapter(getSupportFragmentManager());
        user_image = findViewById(R.id.user_image);
        username  =findViewById(R.id.username);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default"))
                    user_image.setImageResource(R.mipmap.ic_launcher);
                else Glide.with(ContactActivity.this).load(user.getImageURL()).into(user_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter.addFragment(new AllUsersFragment(), "All users");
        adapter.addFragment(new ContactFragment(),"Recent");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}