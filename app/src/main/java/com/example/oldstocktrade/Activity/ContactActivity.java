package com.example.oldstocktrade.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Adapter.ViewPageAdapter;
import com.example.oldstocktrade.Fragment.ContactFragment;
import com.example.oldstocktrade.Fragment.FeedbackFragment;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;
    private CircleImageView user_image;
    private TextView username;
    private ImageButton btn_back;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        tabLayout = findViewById(R.id.tab_contact);
        viewPager = findViewById(R.id.viewpager_id);
        adapter = new ViewPageAdapter(getSupportFragmentManager());
        user_image = findViewById(R.id.user_image);
        username  =findViewById(R.id.username);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
        adapter.addFragment(new ContactFragment(),"Chats");
        adapter.addFragment(new FeedbackFragment(),"Rating");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_conversation);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_star);
        ViewGroup tabs = (ViewGroup) tabLayout.getChildAt(0);

        for (int i = 0; i < tabs.getChildCount() - 1; i++) {
            View tab = tabs.getChildAt(i);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tab.getLayoutParams();
            layoutParams.weight = 1;
            layoutParams.setMarginEnd(12);
            layoutParams.setMarginEnd(12);
            layoutParams.width = 100;
            tab.setLayoutParams(layoutParams);
            tabLayout.requestLayout();
        }
    }
    void status(String s) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", s);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}